'use client';

import React, { useState } from 'react';
import { userAPI } from '@/services/user-api';
import { User } from '@/types/user.types';
import AlertBanner from './AlertBanner';

interface UserSettingsProps {
    user: User;
    onDataChange?: () => void;
}

export default function UserSettings({ user, onDataChange }: UserSettingsProps) {
    const [firstName, setFirstName] = useState(user.firstName || '');
    const [lastName, setLastName] = useState(user.lastName || '');
    const [email, setEmail] = useState(user.email || '');
    const [phoneNumber, setPhoneNumber] = useState(user.phoneNumber || '');
    const [errorMessage, setErrorMessage] = useState('');
    const [successMessage, setSuccessMessage] = useState('');

    const validateEmail = (email: string) => /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email);
    const validatePhone = (phone: string) => /^\+?[\d\s\-()]{7,15}$/.test(phone);

    const handleSave = async () => {
        setErrorMessage('');
        setSuccessMessage('');

        const errors: string[] = [];

        if (!firstName.trim()) errors.push('First name is required');
        if (!email.trim() || !validateEmail(email)) errors.push('Valid email is required');
        if (phoneNumber && !validatePhone(phoneNumber)) errors.push('Phone number is invalid');

        if (errors.length > 0) {
            setErrorMessage(errors.join('. ') + '.');
            return;
        }

        try {
            await userAPI.updateUser(Number(user.id), { firstName, lastName, email, phoneNumber });
            setSuccessMessage('User updated successfully!');
            if (onDataChange) onDataChange();
        } catch (err) {
            console.error(err);
            setErrorMessage('Failed to update user.');
        }
    };

    return (
        <div className="flex flex-col gap-3">
            {(errorMessage || successMessage) && (
                <AlertBanner
                    message={errorMessage || successMessage}
                    type={errorMessage ? 'error' : 'success'}
                />
            )}
            <h3 className="text-3xl font-bold text-[color:var(--dark-blue)]">User Settings</h3>
            <p className="text-gray-600 mb-2">
                Manage your personal information, email address, phone numbers and password.
            </p>

            <form className="flex flex-col gap-3 px-2">
                <div className="flex flex-col md:flex-row gap-4">
                    <div className="flex-1 flex flex-col">
                        <label className="text-[color:var(--dark-blue)] font-bold mb-1">First Name</label>
                        <input
                            type="text"
                            value={firstName}
                            onChange={(e) => setFirstName(e.target.value)}
                            className="border border-gray-300 rounded-lg p-2 focus:outline-none focus:ring-2 focus:ring-accent-primary"
                        />
                    </div>
                    <div className="flex-1 flex flex-col">
                        <label className="text-[color:var(--dark-blue)] font-bold mb-1">Last Name</label>
                        <input
                            type="text"
                            value={lastName}
                            onChange={(e) => setLastName(e.target.value)}
                            className="border border-gray-300 rounded-lg p-2 focus:outline-none focus:ring-2 focus:ring-accent-primary"
                        />
                    </div>
                </div>

                <div className="flex flex-col">
                    <label className="text-[color:var(--dark-blue)] font-bold">Contact Email</label>
                    <input
                        type="email"
                        value={email}
                        onChange={(e) => setEmail(e.target.value)}
                        className="border border-gray-300 rounded-lg p-2 focus:outline-none focus:ring-2 focus:ring-accent-primary"
                    />
                </div>

                <div className="flex flex-col">
                    <label className="text-[color:var(--dark-blue)] font-bold">Phone Number</label>
                    <input
                        type="tel"
                        value={phoneNumber}
                        onChange={(e) => setPhoneNumber(e.target.value)}
                        className="border border-gray-300 rounded-lg p-2 focus:outline-none focus:ring-2 focus:ring-accent-primary"
                    />
                </div>

                <button
                    type="button"
                    onClick={handleSave}
                    className="mt-4 bg-accent-primary text-white font-semibold py-2 px-4 rounded-lg hover:bg-accent-secondary transition-colors"
                >
                    Save Changes
                </button>
            </form>
        </div>
    );
}
