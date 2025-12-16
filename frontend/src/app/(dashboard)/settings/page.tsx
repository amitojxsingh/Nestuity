'use client';

import React, { useState, useEffect } from 'react';
import { userAPI } from '@/services/user-api';
import SettingsSidebar from '@/app/components/SettingsSidebar';
import SettingsHorizonNav from '@/app/components/SettingsHorizonNav';
import UserSettings from '@/app/components/UserSettings';
import BabySettings from '@/app/components/BabySettings';
import NotificationsSettings from '@/app/components/NotificationsSettings';
import { User } from '@/types/user.types';
import LoadingState from '@/app/product/components/LoadingState';
import { useRouter } from "next/navigation";
import { useSession } from "next-auth/react";

export default function TwoCardLayout() {
    const router = useRouter();
    const { data: session, status } = useSession();
    const userId = session?.user?.id;

    const [user, setUser] = useState<User | null>(null);
    const [activePage, setActivePage] = useState<'user' | 'baby' | 'notifications'>('user');
    const [error, setError] = useState<string | null>(null);

    const fetchUser = async () => {
        try {
            if (status === "loading") return;
            if (status === "unauthenticated" || !userId) {
                router.replace("/auth/login");
                return;
            }

            const fetchedUser = await userAPI.getUserById(Number(userId));
            setUser(fetchedUser);
        } catch {
            setError('Failed to load user data. Please try again.');
        }
    };

    useEffect(() => {
        fetchUser();
    }, [userId, status]);

    if (error) return <div className="text-red-600">{error}</div>;
    if (!user) return <LoadingState />;

    const handleDataChange = async () => {
        await fetchUser();
    };

    return (
        <div className="relative min-h-screen">

           <div className="md:hidden">
               <SettingsHorizonNav activePage={activePage} onSelect={setActivePage} />
           </div>

            {/* MAIN CONTENT */}
            <div className="flex items-stretch justify-center gap-3 py-3 px-5 h-[100vh] md:h-auto">

                {/* Sidebar */}
                <div className="hidden md:flex w-1/5 min-h-[calc(100vh-170px)]
">
                    <SettingsSidebar activePage={activePage} onSelect={setActivePage} />
                </div>

                {/* CARD */}
                <div
                    className="
                        flex-1 bg-white rounded-[30px] shadow-lg p-5 flex flex-col
                        overflow-y-auto min-h-0
                        md:h-auto
                        min-h-[calc(100vh-170px)]
                    "
                >
                    {activePage === 'user' && (
                        <UserSettings user={user} onDataChange={handleDataChange} />
                    )}
                    {activePage === 'baby' && (
                        <BabySettings baby={user.babies?.[0]} onDataChange={handleDataChange} />
                    )}
                    {activePage === 'notifications' && (
                        <NotificationsSettings
                            userId={user.id}
                            preferences={user.preferences}
                            onDataChange={handleDataChange}
                        />
                    )}
                </div>

            </div>
        </div>
    );
}
