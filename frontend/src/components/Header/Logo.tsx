'use client';

import Image from 'next/image';
import Link from "next/link";

interface LogoProps {
    href?: string; // optional, defaults to home
}

export default function Logo({ href = '/' }: LogoProps) {
    return (
        <Link href={href} className="flex items-center gap-1">
            <div className="relative h-full w-10">
                <Image
                    src="/logo/svg/logo_mark_color.svg"
                    alt="Nestuity Logo Icon"
                    fill
                    className="object-contain transition-opacity hover:opacity-90"
                    priority
                />
            </div>

            {/* --- Logo Wordmark (hidden on small screens) --- */}
            <div className="hidden sm:block relative w-30 h-full -mb-0.5">
                <Image
                    src="/logo/svg/logo_wordmark_color.svg"
                    alt="Nestuity Logo Wordmark"
                    fill
                    className="object-contain transition-opacity hover:opacity-90"
                    priority
                />
            </div>
        </Link>
    );
}
