'use client';

import "@/styles/globals.css";
import Navbar from '@/components/Header/Navbar';

export default function DashboardLayout({
    children,
}: Readonly<{
    children: React.ReactNode;
}>) {
    return (
        <div>
            <Navbar />

            <main className="flex flex-col min-h-screen bg-primary pt-25">
                {children}
            </main>
        </div>
    );
}