'use client';

import Link from 'next/link';
import Image from 'next/image';
import { LINKS } from '@/constants/links';
import Logo from './Logo';
import SettingsDropdown from './SettingsDropdown';
import HamburgerMenu from '@/app/components/Hamburger';
import Header from './Header';

export default function Navbar() {
    return (
        <Header>
            <Logo href="/dashboard" />

            <div className="flex items-center justify-between w-full">
                {/* Middle / Nav Links (hidden below 932px) */}
                <nav className="hidden lg:flex ml-3 gap-[clamp(0.75rem,2vw,2rem)] whitespace-nowrap">
                    {LINKS.map(({ href, icon, ariaLabel, label }) => (
                        <Link
                            key={href}
                            href={href}
                            aria-label={ariaLabel}
                            className="flex items-center gap-2 p-2 transition-all hover:brightness-150 group no-underline"
                        >
                            <Image
                                src={icon}
                                alt={ariaLabel}
                                width={24}
                                height={24}
                            />
                            <span className="text-sm font-medium text-black">{label}</span>
                        </Link>
                    ))}
                </nav>

                {/* Right: Icons and hamburger */}
                <div className="flex items-center ml-auto">
                    {/* Desktop icons (always visible) */}
                    <SettingsDropdown />
                    {/* Hamburger shows when links are hidden */}
                    <div className="lg:hidden">
                        <HamburgerMenu />
                    </div>
                </div>
            </div>
        </Header>
    );
}
