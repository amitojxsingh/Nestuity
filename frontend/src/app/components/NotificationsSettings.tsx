'use client';

import React, { useState } from 'react';
import { UserPreferences } from '@/types/user.types';
import { userAPI  } from '@/services/user-api';
import AlertBanner from './AlertBanner';

interface NotificationsSettingsProps {
    userId: string; // you need the user id for API call
    preferences?: UserPreferences;
    onDataChange?: () => void;
}

export default function NotificationsSettings({ userId, preferences, onDataChange }: NotificationsSettingsProps) {
    const [emailNotifications, setEmailNotifications] = useState<boolean | undefined>(
        preferences?.emailNotificationsEnabled
    );
    const [smsNotifications, setSmsNotifications] = useState<boolean | undefined>(
        preferences?.smsNotificationsEnabled
    );

    const [errorMessage, setErrorMessage] = useState('');
    const [successMessage, setSuccessMessage] = useState('');

    const handleSave = async () => {
        if (emailNotifications === undefined || smsNotifications === undefined) {
            setErrorMessage('Please select your notification preferences.');
            setSuccessMessage('');
            return;
        }

        try {
            const updatedPreferences = await userAPI.updateUserPreferences(Number(userId), {
                emailNotificationsEnabled: emailNotifications,
                smsNotificationsEnabled: smsNotifications,
                 currency: preferences?.currency ?? 'CAD',
                 timezone: preferences?.timezone ?? 'America/Edmonton',
                });

            setSuccessMessage('Preferences updated successfully!');
            setErrorMessage('');
            if (onDataChange) onDataChange();
        } catch (err) {
            console.error(err);
            setErrorMessage('Failed to update preferences.');
            setSuccessMessage('');
        }
    };

    const AllowButtonGroup = ({
        label,
        allowed,
        setAllowed,
    }: {
        label: string;
        allowed: boolean | undefined;
        setAllowed: (value: boolean) => void;
    }) => {
        return (
            <div className="flex flex-col gap-2">
                <div className="flex justify-between items-center w-full">
                    <span className="text-gray-800 font-medium">{label}</span>
                    <div className="flex w-fit rounded-lg overflow-hidden border border-gray-300">
                        <button
                            type="button"
                            onClick={() => setAllowed(true)}
                            className={`px-5 py-2 font-medium transition-all duration-300 ease-in-out ${
                                allowed === true
                                    ? 'bg-[color:var(--dark-blue)] text-white scale-105 shadow-md'
                                    : 'bg-white text-gray-700 hover:bg-gray-100'
                            }`}
                        >
                            Allow
                        </button>
                        <button
                            type="button"
                            onClick={() => setAllowed(false)}
                            className={`px-5 py-2 font-medium transition-all duration-300 ease-in-out ${
                                allowed === false
                                    ? 'bg-[color:var(--dark-blue)] text-white scale-105 shadow-md'
                                    : 'bg-white text-gray-700 hover:bg-gray-100'
                            }`}
                        >
                            Not Allow
                        </button>
                    </div>
                </div>
            </div>
        );
    };

    return (
        <div className="flex flex-col h-full">
            <form className="flex-1 flex flex-col gap-3 overflow-auto px-1">
                {(errorMessage || successMessage) && (
                    <AlertBanner
                        message={errorMessage || successMessage}
                        type={errorMessage ? 'error' : 'success'}
                    />
                )}

                <h3 className="text-3xl font-bold text-[color:var(--dark-blue)]">Notification Settings</h3>
                <p className="text-gray-600 mb-2">Manage how you receive notifications.</p>

                <AllowButtonGroup
                    label="Email Notifications"
                    allowed={emailNotifications}
                    setAllowed={setEmailNotifications}
                />
                <AllowButtonGroup
                    label="SMS Notifications"
                    allowed={smsNotifications}
                    setAllowed={setSmsNotifications}
                />
            </form>

            <div className="px-2 pb-2">
                <button
                    type="button"
                    onClick={handleSave}
                    className="w-full bg-accent-primary text-white font-semibold py-2 px-4 rounded-lg hover:bg-accent-secondary transition-colors"
                >
                    Save Changes
                </button>
            </div>
        </div>
    );
}
