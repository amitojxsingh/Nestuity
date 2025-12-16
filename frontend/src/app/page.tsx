'use client';

import Image from "next/image";
import { detailedFeatures } from "@/data/detailedFeatures";
import Link from "next/link";
import Header from "@/components/Header/Header";
import Logo from "@/components/Header/Logo";
import Footer from "@/components/Footer";

export default function Home() {
    return (
        <div>
            <main className="grid grid-cols-1 gap-30 pb-16">
                <Header>
                    <Logo />
                    <div className="flex w-full justify-end gap-3">
                        <Link
                            href="/auth/login"
                            className="text-accent-primary hover:underline content-center text-nowrap"
                        >
                            Log In
                        </Link>
                        <Link
                            href="/auth/register"
                            className="bg-accent-primary text-white px-2 py-0.5 rounded-full hover:bg-accent-primary/80 transition content-center text-nowrap"
                        >
                            Get Started
                        </Link>
                    </div>
                </Header>

                {/* Hero */}
                <section className="section-layout layout gradient-hero pt-20 text-white pb-10">
                    <div className="grid grid-cols-1 gap-6 md:my-20 md:grid-cols-2 md:grid-rows-[auto_auto_1fr] md:items-start md:gap-10">
                        {/* 1) Header (text) */}
                        <div className="order-1 pt-10 md:row-start-1">
                            <h1 className="text-3xl md:text-4xl font-bold">
                                Discover the Ultimate Parenting Companion: Save Time, Money, and Stress
                            </h1>
                            <p className="mt-3 max-w-prose text-base text-white/90">
                                Nestuity empowers parents by simplifying the management of baby essentials and routines. 
                                Our platform ensures you never miss a milestone while keeping your budget in check.
                            </p>
                        </div>

                        {/* 2) Button */}
                        <div className="order-2 justify-self-center md:justify-self-start md:row-start-2">
                            <Link
                                href="/auth/register"
                                className="getting-started-button font-semibold inline-block shadow-md"
                            >
                                Get Started
                            </Link>
                        </div>

                        {/* 3) Image (mobile: appears after button; desktop: spans right column rows 1-3) */}
                        <div className="order-3 self-center md:order-none md:row-start-1 md:row-span-3 md:col-start-2 md:col-end-3">
                            <div className="relative w-full max-w-[628px] aspect-square rounded overflow-hidden md:ml-auto">
                                <Image
                                    src="/images/landing/hero-image.jpg"
                                    alt="Mother and Child"
                                    fill
                                    className="object-cover rounded-[50px]"
                                    priority
                                />
                            </div>
                        </div>

                        {/* 4) Features (mobile: below image; desktop: left column row 3 under button) */}
                        <div className="order-4 md:row-start-3">
                            <div className="grid grid-cols-1 gap-2 md:grid-cols-2">
                                <div className="feature">
                                    <Image src="/icons/svg/clock-purple.svg" alt="Clock" width={50} height={50} />
                                    <h3 className="text-accent-secondary">Time Savings</h3>
                                    <p className="text-black">
                                        Effortlessly track milestones and daily tasks with our customizable reminders tailored for you.
                                    </p>
                                </div>

                                <div className="feature">
                                    <Image src="/icons/svg/savings-purple.svg" alt="Savings" width={50} height={50} />
                                    <h3 className="text-accent-secondary">Cost Efficiency</h3>
                                    <p className="text-black">
                                        Utilize AI-driven insights to make informed purchases and save on baby essentials.
                                    </p>
                                </div>
                            </div>
                        </div>
                    </div>
                </section>







                {/* Detailed Features */}
                <section id="detailed-features" className="py-16 bg-gradient-to-b from-background to-white">
                    <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
                        <div className="text-center mb-12">
                            <div className="inline-block font-semibold bg-accent-secondary/10 text-accent-secondary px-4 py-2 rounded-full mb-4">
                                Features
                            </div>
                            <h1 className="font-bold text-4xl md:text-5xl mb-4 text-foreground">
                                Discover Our Essential Parenting Tools
                            </h1>
                            <p className="text-lg text-gray-600 max-w-2xl mx-auto">
                                Nestuity offers a suite of features designed to simplify parenting.
                                From reminders to usage calculators, we help you stay organized and
                                focused on what matters most.
                            </p>
                        </div>

                        <div className="grid grid-cols-1 md:grid-cols-3 gap-8 mb-12">
                            {[...detailedFeatures].map((feature, i) => (
                                <div
                                    key={i}
                                    className="group bg-white p-8 rounded-2xl shadow-lg hover:shadow-2xl transition-all duration-300 hover:-translate-y-2 border border-gray-100"
                                >
                                    <div className="flex justify-center mb-6">
                                        <div className="p-4 bg-gradient-to-br from-accent-primary to-accent-secondary rounded-full group-hover:scale-110 transition-transform duration-300">
                                            <Image
                                                src={feature.icon}
                                                alt={feature.title}
                                                width={48}
                                                height={48}
                                                className="invert"
                                            />
                                        </div>
                                    </div>
                                    <h2 className="text-xl font-bold text-accent-secondary mb-3 text-center">
                                        {feature.title}
                                    </h2>
                                    <p className="text-gray-600 text-center leading-relaxed">
                                        {feature.text}
                                    </p>
                                </div>
                            ))}
                        </div>

                        <div className="text-center">
                            <Link href={"/auth/register"} className="getting-started-button-enhanced">
                                Get Started
                            </Link>
                        </div>
                    </div>
                </section>
            </main>

            <Footer />
        </div>
    );
}
