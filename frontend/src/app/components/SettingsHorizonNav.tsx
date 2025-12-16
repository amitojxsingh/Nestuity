'use client';

import React from 'react';

interface SettingsHorizonNavProps {
    activePage: 'user' | 'baby' | 'notifications';
    onSelect: (page: 'user' | 'baby' | 'notifications') => void;
}

const icons = {
    user: '/images/user-setting/user_setting.png',
    baby: '/images/user-setting/baby_setting.png',
    notifications: '/images/user-setting/notification_setting.png',
};

const settingsItems = [
    { key: 'user', title: 'User', icon: icons.user },
    { key: 'baby', title: 'Baby', icon: icons.baby },
    { key: 'notifications', title: 'Notifications', icon: icons.notifications },
];

export default function SettingsHorizonNav({ activePage, onSelect }: SettingsHorizonNavProps) {
    return (
        <div
            className="
                md:hidden
                bg-white
                rounded-2xl
                shadow-lg
                border border-gray-200
                flex justify-around items-center
                py-2 px-4
                w-[88%]
                max-w-sm
                mx-auto
                transition-all
            "
        >
            {settingsItems.map((item) => (
                <button
                    key={item.key}
                    onClick={() => onSelect(item.key as any)}
                    className="flex flex-col items-center justify-center focus:outline-none"
                >
                    <img
                        src={item.icon}
                        alt={item.title}
                        className={`h-6 w-auto mb-1 object-contain transition-transform ${
                            activePage === item.key ? "scale-110" : ""
                        }`}
                    />
                    <span
                        className={`text-xs font-bold ${
                            activePage === item.key ? "text-accent-primary" : "text-gray-500"
                        }`}
                    >
                        {item.title}
                    </span>
                </button>
            ))}
        </div>
    );
}

