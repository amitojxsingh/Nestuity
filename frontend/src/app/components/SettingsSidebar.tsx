'use client';

import React from 'react';

interface SettingsSidebarProps {
    activePage: 'user' | 'baby' | 'notifications';
    onSelect: (page: 'user' | 'baby' | 'notifications') => void;
}

const icons = {
    user: '/images/user-setting/user_setting.png',
    baby: '/images/user-setting/baby_setting.png',
    notifications: '/images/user-setting/notification_setting.png',
};

const settingsItems = [
    { key: 'user', title: 'User Settings', icon: icons.user },
    { key: 'baby', title: 'Baby Settings', icon: icons.baby },
    { key: 'notifications', title: 'Notification Settings', icon: icons.notifications },
];

export default function SettingsSidebar({ activePage, onSelect }: SettingsSidebarProps) {
  return (
    <div className="bg-white rounded-[30px] shadow-lg flex flex-col p-1 h-full overflow-y-auto">
      {/* Header */}
      <h2 className="text-4xl font-bold mb-3 text-[color:var(--dark-blue)] text-center">Settings</h2>

      {/* Divider line */}
      <div className="border-b border-gray-300 mb-3"></div>

      {/* Sidebar buttons */}
      <div className="flex flex-col gap-2">
        {settingsItems.map((item) => (
          <button
            key={item.key}
            onClick={() => onSelect(item.key as any)}
            className={`flex items-center rounded-xl transition-colors duration-200 ${
              activePage === item.key ? 'bg-accent-primary text-white' : 'hover:bg-gray-100 text-gray-700'
            }`}
          >
            {/* Icon + Text wrapper */}
            <div className="flex items-center py-2 pl-2 pr-3 gap-1.5">
              <img src={item.icon} alt={item.title} className="w-6 h-6 object-contain" />
              <span
                className="font-semibold text-base break-words overflow-hidden max-w-full"
                style={{ display: '-webkit-box', WebkitLineClamp: 2, WebkitBoxOrient: 'vertical' }}
                title={item.title}
              >
                {item.title}
              </span>
            </div>
          </button>
        ))}
      </div>
    </div>
  );
}