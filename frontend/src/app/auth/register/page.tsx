"use client";

import { useState } from "react";
import Link from "next/link";
import Image from "next/image";
import {userAPI} from "@/services/user-api";
import {NewUser} from "@/types/user.types";
import { useRouter } from "next/navigation";
import {useSession, signIn, signOut} from "next-auth/react";

export default function RegisterPage() {
    const [form, setForm] = useState({ firstName: "", lastName: "", email: "", password: "", confirmPassword: "" });
    const [loading, setLoading] = useState<boolean>(false);
    const [error, setError] = useState<string | null>(null);
    const {data: session, status} = useSession();
    const router = useRouter();

    const passwordRules = [
        { label: "One uppercase letter", test: (p: string) => /[A-Z]/.test(p) },
        { label: "One lowercase letter", test: (p: string) => /[a-z]/.test(p) },
        { label: "One number", test: (p: string) => /\d/.test(p) },
        { label: "One special character (#, !, etc.)", test: (p: string) => /[^A-Za-z0-9]/.test(p) },
    ];
    const allRulesMet = passwordRules.every(r => r.test(form.password));

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();
        setError(null);
        setLoading(true);

        // Validate form
        if (!allRulesMet) {
            setError("Password does not meet all requirements");
            setLoading(false);
            return;
        }
        if (form.password !== form.confirmPassword) {
            setError("Passwords do not match");
            setLoading(false);
            return;
        }

        try{
            const newUser: NewUser = {
                firstName: form.firstName,
                lastName: form.lastName,
                email: form.email,
                password: form.password,
            };
            const savedUser = await userAPI.createUser(newUser);
            const result = await signIn("credentials", {
                email: savedUser.email,
                password: form.password,
                redirect: false,
            });
            if (result?.ok) {
                router.push("/auth/b-stats");
            }
        } catch (error) {
            setError((error as Error).message);
            console.log(error);
        }finally{
            setLoading(false);
        }
    };

    return (
        <section className="h-screen flex flex-row md:flex-row items-center justify-evenly bg-gradient-to-b from-secondary to-accent-primary text-foreground px-6">
            <div className="max-w-dvh w-full max-h-[calc(100vh-6rem)] no-scrollbar overflow-y-auto bg-white rounded-2xl shadow-lg p-8 space-y-4">
                <h1 className="font-bold text-center text-accent-primary">
                    Create Account
                </h1>

                <form onSubmit={handleSubmit} className="flex flex-col gap-2">
                    <div className={"flex gap-4 flex-wrap"}>
                        <label className="flex flex-1 flex-col text-black/50">First Name
                        <input
                            type="text"
                            placeholder="First Name"
                            disabled={loading}
                            className="border border-gray-300 rounded-lg px-4 py-2 focus:outline-none focus:ring-2 focus:ring-[var(--color-accent-primary)]"
                            value={form.firstName}
                            onChange={(e) => setForm({ ...form, firstName: e.target.value })}
                        />
                        </label>

                        <label className="flex flex-1 flex-col text-black/50">Last Name
                        <input
                            type="text"
                            placeholder="Last Name"
                            disabled={loading}
                            className="border border-gray-300 rounded-lg px-4 py-2 focus:outline-none focus:ring-2 focus:ring-[var(--color-accent-primary)]"
                            value={form.lastName}
                            onChange={(e) => setForm({ ...form, lastName: e.target.value })}
                        />
                        </label>
                    </div>

                    <label className="flex flex-col text-black/50">Email
                    <input
                        type="email"
                        placeholder="Email"
                        className="border border-gray-300 rounded-lg px-4 py-2 focus:outline-none focus:ring-2 focus:ring-[var(--color-accent-primary)]"
                        value={form.email}
                        onChange={(e) => setForm({ ...form, email: e.target.value })}
                    />
                    </label>

                    <label className="flex flex-col text-black/50">Password
                    <input
                        type="password"
                        placeholder="Password"
                        className="border border-gray-300 rounded-lg px-4 py-2 focus:outline-none focus:ring-2 focus:ring-[var(--color-accent-primary)]"
                        value={form.password}
                        onChange={(e) => setForm({...form, password: e.target.value })}
                    />
                    <div className={"flex flex-wrap"}>
                        <div className={"w-1/2"}>
                            {passwordRules.slice(0, 2).map((rule) => {
                                const met = rule.test(form.password);
                                return (
                                    <div
                                        key={rule.label}
                                        className={`flex ${met ? "text-green-600" : "text-gray-500"}`}
                                    >
                                        {rule.label}
                                        {met && (
                                            <Image
                                                src="/icons/svg/check_24dp.svg"
                                                alt="Check"
                                                width={20}
                                                height={20}
                                            />
                                        )}
                                    </div>
                                );
                            })}
                        </div>
                        <div className={"w-1/2"}>
                            {passwordRules.slice(2, 5).map((rule) => {
                                const met = rule.test(form.password);
                                return (
                                    <div
                                        key={rule.label}
                                        className={`flex ${met ? "text-green-600" : "text-gray-500"}`}
                                    >
                                        {rule.label}
                                        {met && (
                                            <Image
                                                src="/icons/svg/check_24dp.svg"
                                                alt="Check"
                                                width={20}
                                                height={20}
                                            />
                                        )}
                                    </div>
                                );
                            })}
                        </div>
                    </div>
                    </label>

                    <label className="flex flex-col text-black/50">Confirm Password
                    <input
                        type="password"
                        placeholder="Confirm Password"
                        className="border border-gray-300 rounded-lg px-4 py-2 focus:outline-none focus:ring-2 focus:ring-[var(--color-accent-primary)]"
                        value={form.confirmPassword}
                        onChange={(e) => setForm({ ...form, confirmPassword: e.target.value })}
                    />
                    </label>

                    {error && <p className="text-red-500 text-sm">{error}</p>}
                    <button
                        type="submit"
                        disabled={loading}
                        className="bg-[var(--color-accent-primary)] text-white rounded-lg py-2 hover:bg-[var(--color-accent-secondary)] transition-colors text-center"
                    >
                        {loading ? "Registering..." : "Register"}
                    </button>
                </form>

                <p className="text-center text-sm">
                    Already have an account?{" "}
                    <Link href={"/auth/login"} className="text-[var(--color-accent-primary)] hover:underline">
                        Log in
                    </Link>
                </p>
            </div>

            <Image
                src={"/logo/other/logo2_white.png"}
                alt={"Nestuity Logo 2"}
                width={600}
                height={600}
                className="hidden lg:block w-full h-auto max-w-[600px] object-contain"
            />
        </section>
    );
}
