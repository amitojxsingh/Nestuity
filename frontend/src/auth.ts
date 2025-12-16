import NextAuth from "next-auth"
import Credentials from "next-auth/providers/credentials"
import Google from "next-auth/providers/google";
import {LoginRequest, NewUser} from "@/types/user.types";
import {userAPI} from "@/services/user-api";

export const { handlers, signIn, signOut, auth } = NextAuth({
    trustHost: true,
    providers: [
        Google({
            authorization: {
                params: {
                    prompt: "consent select_account",
                    access_type: "offline",
                    response_type: "code",
                }
            }
        }),
        Credentials({
            name: "Credentials",
            credentials: {
                email: { label: "Email", type: "email" },
                password: { label: "Password", type: "password" },
            },

            authorize: async (credentials) => {
                // ===== TEST / CI MODE: auto-login =====
                if (process.env.CI === "true" || process.env.CYPRESS === "true") {
                return {
                    id: "1",
                    email: credentials?.email || "test@test.com",
                    firstName: "Test",
                    lastName: "User",
                };
                }

                try {
                    // Use internal Docker network URL for server-side API calls
                    const apiUrl = process.env.BACKEND_INTERNAL_URL ||
                                   process.env.NEXT_PUBLIC_API_URL?.replace('/api', '') ||
                                   'http://localhost:8080';
                    const loginUrl = `${apiUrl}/api/users/login`;
                    const loginRequest: LoginRequest = {
                        email: credentials.email as string,
                        password: credentials.password as string,
                    };


                    const res = await fetch(loginUrl, {
                        method: "POST",
                        headers: {
                            "Content-Type": "application/json",
                        },
                        body: JSON.stringify(loginRequest)
                    });

                    if (!res.ok) {
                        const errorData = await res.json().catch(() => ({}));
                        console.log("Login failed:", errorData);
                        return null;
                    }

                    const user = await res.json();

                    if (!user || !user.id) {
                        return null;
                    }

                    return {
                        id: String(user.id),
                        email: user.email,
                        firstName: user.firstName,
                        lastName: user.lastName,
                    };

                } catch (error) {
                    console.error("Login failed:", error);
                    return null;
                }
            },
        })
    ],
    session: {
        strategy: "jwt",
        maxAge: 60 * 60 * 24,  // 1 day
    },
    callbacks: {
        authorized: async ({ auth }) => {
            if (process.env.CI === "true" || process.env.CYPRESS === "true") {
                return true;
            }
            // Logged in users are authenticated, otherwise redirect to login page
            return !!auth
        },
        async jwt({ token, user, account, profile }) {
            if (account?.provider === "google" && profile && profile.email) {
                try {
                    // Use internal Docker network URL for server-side API calls
                    const apiUrl = process.env.BACKEND_INTERNAL_URL ||
                                   process.env.NEXT_PUBLIC_API_URL?.replace('/api', '') ||
                                   'http://localhost:8080';
                    const createUrl = `${apiUrl}/api/users`;
                    const newUser: NewUser = {
                        email: profile.email,
                        firstName: profile.given_name ?? "",
                        lastName: profile.family_name ?? "",
                        authProvider: "google",
                        providerId: profile.sub!,
                    };
                    await fetch(createUrl, {
                        method: "POST",
                        headers: { "Content-Type": "application/json" },
                        body: JSON.stringify(newUser),
                    });
                } catch (err) {
                    console.warn("Failed to sync Google user:", err);
                }

                token.id = profile.sub;
                token.email = profile.email;
                token.firstName = profile.given_name;
                token.lastName = profile.family_name;
            }

            if (user && account?.provider === "credentials") {
                token.id = user.id;
                token.email = user.email;
                token.firstName = user.firstName;
                token.lastName = user.lastName;
            }

            return token;
        },
        async session({ session, token }) {
            // Add token info to session
            if (session.user) {
                session.user.id = token.id as string;
                session.user.email = token.email as string;
                session.user.firstName = token.firstName as string;
                session.user.lastName = token.lastName as string;
            }
            return session;
        },
    },
    pages: {
        signIn: "/auth/login",
    },
    debug: process.env.ENV_VARIABLE === "dev",
})