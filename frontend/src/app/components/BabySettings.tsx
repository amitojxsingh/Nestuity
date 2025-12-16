'use client';

import React, { useState, useEffect } from 'react';
import { Baby } from '@/types/user.types';
import { babyApi } from '@/services/baby-api';
import AlertBanner from './AlertBanner';

interface BabySettingsProps {
    baby?: Baby;
    onDataChange?: () => void;
}

export default function BabySettings({ baby, onDataChange }: BabySettingsProps) {
    // Get the first baby if exists, otherwise use empty defaults
    const firstBaby: Baby | undefined = baby;
    const [errorMessage, setErrorMessage] = useState('');
    const [successMessage, setSuccessMessage] = useState('');
    const [babyName, setBabyName] = useState(firstBaby?.name || '');
    const [birthDate, setBirthDate] = useState(firstBaby?.dob?.slice(0, 10) || ''); // format YYYY-MM-DD
    const [weight, setWeight] = useState(firstBaby?.weight?.toString() || '');
    const [diaperSize, setDiaperSize] = useState(firstBaby?.diaperSize || '');
    const [dailyUsage, setDailyUsage] = useState(firstBaby?.dailyUsage || 0);

    const handleSave = async () => {
        const errors: string[] = [];
        if (!babyName.trim()) errors.push("name");
        if (!birthDate) errors.push("birth date");
        if (!weight || isNaN(parseFloat(weight)) || parseFloat(weight) <= 0) errors.push("weight");
        if (!diaperSize) errors.push("diaper size");

        if (errors.length > 0) {
            setErrorMessage(`Please provide a valid ${errors.join(', ')}.`);
            return;
        }

        if (!firstBaby) {
            setErrorMessage('No baby found to update.');
            return;
        }

        try {
            const updatedBaby = await babyApi.update(firstBaby.id, {
                name: babyName,
                dob: birthDate,
                weight: parseFloat(weight),
                dailyUsage: dailyUsage,
                diaperSize,
            });

            setSuccessMessage('Baby updated successfully!');
            setErrorMessage('');
            if (onDataChange) onDataChange();
        } catch (err) {
            console.error(err);
            setErrorMessage('Failed to update baby.');
            setSuccessMessage('');
        }
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
                <h3 className="text-3xl font-bold text-[color:var(--dark-blue)]">Baby Settings</h3>
                <p className="text-gray-600 mb-2">
                    Manage your baby's information and preferences below.
                </p>

                {/* Baby Name */}
                <div className="flex flex-col">
                    <label className="text-[color:var(--dark-blue)] font-bold mb-1" htmlFor="babyName">Baby's Name</label>
                    <input
                        type="text"
                        id="babyName"
                        value={babyName}
                        onChange={(e) => setBabyName(e.target.value)}
                        className="border border-gray-300 rounded-lg p-2 focus:outline-none focus:ring-2 focus:ring-accent-primary"
                        placeholder="Enter baby's name"
                    />
                </div>

                {/* Birth Date */}
                <div className="flex flex-col">
                    <label className="text-[color:var(--dark-blue)] font-bold mb-1" htmlFor="birthDate">Birth Date</label>
                    <input
                        type="date"
                        id="birthDate"
                        value={birthDate}
                        onChange={(e) => setBirthDate(e.target.value)}
                        className="border border-gray-300 rounded-lg p-2 focus:outline-none focus:ring-2 focus:ring-accent-primary"
                    />
                </div>

                {/* Weight & Diaper Size */}
                <div className="flex flex-col gap-0 mt-1 mb-3">
                    <div className="text-[color:var(--dark-blue)] font-semibold text-xl">Baby's Diaper Usage</div>
                    <span className="text-sm text-gray-500 mb-1">Help us track your baby's diaper needs!</span>
                    <div className="flex flex-col md:flex-row gap-4">
                        {/* Weight */}
                        <div className="flex-1 flex flex-col">
                            <label className="text-[color:var(--dark-blue)] font-bold mb-1" htmlFor="weight">Weight (kg)</label>
                            <input
                                type="number"
                                id="weight"
                                value={weight}
                                onChange={(e) => setWeight(e.target.value)}
                                className="border border-gray-300 rounded-lg p-2 focus:outline-none focus:ring-2 focus:ring-accent-primary"
                                placeholder="e.g. 3.5"
                            />
                        </div>

                        {/* Diaper Size */}
                        <div className="flex-1 flex flex-col">
                            <label className="text-[color:var(--dark-blue)] font-bold mb-1" htmlFor="diaperSize">Diaper Size</label>
                            <select
                                id="diaperSize"
                                value={diaperSize}
                                onChange={(e) => setDiaperSize(e.target.value)}
                                className="border border-gray-300 rounded-lg p-2 focus:outline-none focus:ring-2 focus:ring-accent-primary"
                            >
                                <option value="">Select size</option>
                                <option value="newborn">Newborn</option>
                                <option value="1">1</option>
                                <option value="2">2</option>
                                <option value="3">3</option>
                                <option value="4">4</option>
                                <option value="5">5</option>
                                <option value="6">6</option>
                            </select>
                        </div>
                    </div>
                </div>
            </form>

            {/* Save button */}
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
