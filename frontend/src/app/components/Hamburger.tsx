'use client';

import { useState, useRef, useEffect } from 'react';
import Hamburger from 'hamburger-react';
import Link from 'next/link';
import Image from 'next/image';
import { LINKS } from '../../constants/links';

export default function HamburgerMenu() {
    const [isOpen, setOpen] = useState(false);
    const containerRef = useRef<HTMLDivElement>(null); // wrap button + menu

    // Close dropdown if clicked outside
    useEffect(() => {
        function handleClickOutside(event: MouseEvent) {
        if (containerRef.current && !containerRef.current.contains(event.target as Node)) {
            setOpen(false);
        }
        }
        document.addEventListener('mousedown', handleClickOutside);
        return () => document.removeEventListener('mousedown', handleClickOutside);
    }, []);

    return (
        <div ref={containerRef}>
            {/* Hamburger button */}
            <div className="icon-container">
                <Hamburger
                    toggled={isOpen}
                    toggle={setOpen}
                    size={20}
                />
            </div>

            {/* Dropdown menu */}
            <div
                className={`
                    absolute left-0 top-full mt-2 w-full bg-background rounded-xl shadow-lg
                    transition-all duration-300 ease-in-out
                    ${isOpen ? 'opacity-100 translate-y-0 pointer-events-auto' : 'opacity-0 -translate-y-2 pointer-events-none'}
                `}
            >
                <nav className="flex flex-col py-2 px-3">
                {LINKS.map(({ href, label, icon, ariaLabel }) => (
                    <Link
                        key={href}
                        href={href}
                        onClick={() => setOpen(false)}
                        className="flex items-center gap-3 py-2 px-2 hover:bg-light-grey rounded-lg transition-colors"
                    >
                        <Image
                            src={icon}
                            alt={ariaLabel}
                            width={20}
                            height={20}
                        />
                        <span>{label}</span>
                    </Link>
                ))}
                </nav>
            </div>
        </div>
    );
}
