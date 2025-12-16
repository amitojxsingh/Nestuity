"use client";

import {useEffect, useState} from "react";
import Link from "next/link";
import Image from "next/image";
import { useRouter } from "next/navigation";
import {signIn, useSession} from "next-auth/react";

export default function LoginPage() {
    const router = useRouter();
    const {data: session, status} = useSession();
    const [loading, setLoading] = useState<boolean>(false);
    const [error, setError] = useState<string | null>(null);
    const [form, setForm] = useState({
        email: "",
        password: "",
    });

    useEffect(() => {
        if (status === "authenticated" || session) {
            router.push("/dashboard");
        }
    }, [status, session]);

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();
        setError(null);
        setLoading(true);

        try {
            const loggedInUser = await signIn("credentials", {
                email: form.email,
                password: form.password,
                redirect: false,
            });
            if (loggedInUser?.error === "CredentialsSignin") {
                setError("Invalid credentials");
            } else if (loggedInUser?.ok) {
                console.log(loggedInUser)
                router.push("/dashboard");
            }
        } catch (error) {
            setError((error as Error).message);
            console.log(error);
        } finally {
            setLoading(false);
        }
    };

    return (
        <section className="h-screen flex flex-row items-center justify-evenly bg-background text-foreground px-6">
            <div className="max-w-dvh w-full max-h-[calc(100vh-6rem)] no-scrollbar overflow-y-auto bg-gradient-to-b from-secondary to-accent-primary rounded-2xl shadow-lg p-8 space-y-4 text-white">
                <h1 className="text-highlight font-bold text-center">
                    Log In
                </h1>

                <form onSubmit={handleSubmit} className="flex flex-col gap-4">
                    <label className="flex flex-col text-white/50">Email
                    <input
                        type="email"
                        disabled={loading}
                        placeholder="Email"
                        className="border border-gray-300 rounded-lg px-4 py-2 focus:outline-none text-white"
                        value={form.email}
                        onChange={(e) => setForm({ ...form, email: e.target.value })}
                    />
                    </label>

                    <label className="flex flex-col text-white/50">Password
                    <input
                        type="password"
                        disabled={loading}
                        placeholder="Password"
                        className="border border-gray-300 rounded-lg px-4 py-2 focus:outline-none text-white"
                        value={form.password}
                        onChange={(e) => setForm({ ...form, password: e.target.value })}
                    />
                    </label>

                    {error && <p className="text-red-500 text-sm">{error}</p>}

                    <button
                        type="submit"
                        disabled={loading}
                        className="bg-accent-primary text-white rounded-lg py-2 hover:bg-accent-secondary transition-colors text-center"
                    >
                        {loading ? "Signing in..." : "Sign In"}
                    </button>
                </form>

                <p className="text-center text-sm">
                    Don’t have an account?{" "}
                    <Link href={"/auth/register"} className="text-[var(--color-primary)] hover:underline">
                        Register
                    </Link>
                </p>
            </div>
            <Image
                src={"/logo/other/logo2_colour.png"}
                alt={"Nestuity Logo 2"}
                width={600}
                height={600}
                className="hidden lg:block w-full h-auto max-w-[600px] object-contain"
            />
        </section>
    );
}
