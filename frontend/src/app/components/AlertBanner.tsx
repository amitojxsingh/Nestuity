'use client';

import React from 'react';

interface AlertBannerProps {
    message: string;
    type?: 'success' | 'error';
}

export default function AlertBanner({ message, type = 'error' }: AlertBannerProps) {
    if (!message) return null;

    const iconSrc = type === 'error' ? "/icons/svg/warning.svg" : "/icons/svg/success.svg";

    return (
        <div className="mb-3 flex items-center gap-2 rounded-md bg-[var(--light-blue)] text-[var(--dark-blue)] px-3 py-2 text-sm shadow-sm">
            <img
                src={iconSrc}
                alt={type === 'error' ? "Warning icon" : "Success icon"}
                className="w-4 h-4 flex-shrink-0"
            />
            <span className="font-medium leading-snug">{message}</span>
        </div>
    );
}
