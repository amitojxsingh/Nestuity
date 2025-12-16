'use client';

import Image from 'next/image';
import Link from "next/link";

export default function LandingNavbar() {
    return (
        <header className="flex items-center justify-between px-8 py-2 bg-[var(--color-secondary)] sticky top-0 z-50">
            <div className="flex items-center gap-3 h-20 overflow-hidden">
                {/* Logo */}
                <Image
                    src="/logo/svg/logo1_white.svg"
                    alt="Nestuity Logo"
                    width={300}
                    height={100}
                    className="cursor-pointer hover:fill-[var(--color-accent-secondary)]"
                />
                {/* Navigation Links */}
                <nav className="hidden md:flex items-center gap-8 text-[var(--color-highlight)] text-lg">
                    <Link href={"/product/1"} className="hover:text-[var(--color-accent-secondary)]">Try It Out</Link>
                {/*    <a href="#features" className="hover:text-[var(--color-accent-secondary)]">Features</a>*/}
                {/*    <a href="#about" className="hover:text-[var(--color-accent-secondary)]">About</a>*/}
                {/*    <a href="#contact" className="hover:text-[var(--color-accent-secondary)]">Contact</a>*/}
                </nav>
            </div>


            {/* Auth Buttons */}
            <div className="flex items-center gap-4">
                <Link
                    className="px-3 py-2 w-40 text-[var(--color-highlight)] rounded-3xl border-2 font-medium hover:text-[var(--color-accent-secondary)] transition-colors cursor-pointer text-center"
                    href={"/auth/login"}
                >
                    Log In
                </Link>
                <Link
                    className="px-3 py-2 w-40 bg-[var(--color-highlight)] text-[var(--color-secondary)] rounded-3xl hover:bg-[var(--color-accent-secondary)] hover:text-[var(--color-highlight)] transition-colors cursor-pointer text-center"
                    href={"/auth/register"}
                >
                    Get Started
                </Link>
            </div>
        </header>
    );
}
