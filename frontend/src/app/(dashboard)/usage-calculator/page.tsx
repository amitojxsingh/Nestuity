'use client';

import { useState, useEffect } from 'react';
import { babyApi, diaperApi } from '@/services/baby-api';
import { Baby, DiaperUsage } from '@/types/baby.types';
import LoadingState from '@/app/product/components/LoadingState';
import ErrorState from '@/app/product/components/ErrorState';
import { useSession } from 'next-auth/react';
import { useRouter } from 'next/navigation';

export default function Calculator() {
    const [activeField, setActiveField] = useState<string | null>(null);
    const [diaperUsage, setDiaperUsage] = useState<DiaperUsage | null>(null);
    const [remainingDiapers, setRemainingDiapers] = useState<number>();
    const [baby, setBaby] = useState<Baby | null>(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState<string | null>(null);

    const [vars, setVars] = useState({
        "Baby's Age": { value: [0, 'M'], image: '/images/usage_calc/baby_age.png' },
        "Average Diaper Usage per Day": { value: [0, ''], image: '/images/usage_calc/diaper_usage.png' },
        "Baby's Weight": { value: [0, ' kgs'], image: '/images/usage_calc/baby_weight.png' },
        "Diaper Size": { value: ['', ''], image: '/images/usage_calc/diaper_size.png' },
        "Number of Diapers per Box": { value: [0, ''], image: '/images/usage_calc/diaper_per_box.png' },
        "Diapers at Home": { value: [0, ' boxes'], image: '/images/usage_calc/diaper_at_home.png' },
    });

    const router = useRouter();
    const { data: session, status } = useSession();
    const userId = session?.user?.id;
    const [babyId, setBabyId] = useState<number | null>(null);

    useEffect(() => {
        if (status === 'loading') return;
        if (status === 'unauthenticated' || !userId) {
            router.replace('/auth/login');
            return;
        }

        const init = async () => {
            try {
                setLoading(true);
                const babies = await babyApi.getByUserId(Number(userId));
                if (!babies || babies.length === 0) return;
                const babyIdFetched = babies[0].id;
                setBabyId(babyIdFetched);

                const babyData = await babyApi.getByBabyId(babyIdFetched);
                const diaperData = await diaperApi.getDiaperUsage(babyIdFetched);

                const inventory = await diaperApi.getUserInventory(Number(userId));
                const diaperItem = inventory.find(item => item.supplyName.toLowerCase() === 'diapers');

                const totalDiapers = diaperItem?.totalSingleQuantity || 0; // individual diapers
                const totalDiaperBoxes = diaperItem?.totalUnitQuantity || 0;    // boxes
                const diapersPerBox = diaperItem?.unitConversion || 0;   // diapers per box

                babyData.dob = new Date(babyData.dob).toISOString();
                setBaby(babyData);
                setDiaperUsage(diaperData);

                const age = monthDiff(babyData.dob);

                setVars({
                    "Baby's Age": { value: [age, 'M'], image: '/images/usage_calc/baby_age.png' },
                    "Average Diaper Usage per Day": { value: [babyData.dailyUsage, ''], image: '/images/usage_calc/diaper_usage.png' },
                    "Baby's Weight": { value: [babyData.weight, ' kgs'], image: '/images/usage_calc/baby_weight.png' },
                    "Diaper Size": { value: [babyData.diaperSize, ''], image: '/images/usage_calc/diaper_size.png' },
                    "Number of Diapers per Box": { value: [diapersPerBox, ''], image: '/images/usage_calc/diaper_per_box.png' },
                    "Diapers at Home": { value: [Math.round(totalDiaperBoxes * 10) / 10, ' boxes'], image: '/images/usage_calc/diaper_at_home.png' },
                });

                setError(null);
            } catch (err) {
                console.error('Error fetching baby:', err);
                setError('Failed to load baby data. Please try again later.');
            } finally {
                setLoading(false);
            }
        };

        init();
    }, [userId, status]);

    const diaperLast = diaperUsage ? diaperUsage.daysLeft : 0;
    const diaperBoxes = diaperUsage ? diaperUsage.recommendedPurchase : 0;

    const monthDiff = (dob: string): number => {
        const now = new Date();
        const birthday = new Date(dob);
        const years = now.getFullYear() - birthday.getFullYear();
        const months = now.getMonth() - birthday.getMonth();
        let diff = years * 12 + months;
        if (now.getDate() < birthday.getDate()) diff -= 1;
        return diff;
    };

    if (loading) return <LoadingState />;
    if (error || !vars) return <ErrorState message={error || 'Calculator data not found'} />;


    return (
        <div className="w-full">
              <div className="flex flex-col h-full items-center gap-15 my-8 md:my-0 mx-5">
                <div className="widget w-full flex flex-col mt-3 mb-4 min-h-[82vh] p-4">
                {/* Compact Header Bar */}
                <div className="w-full flex items-center justify-between bg-accent-primary p-3 rounded-xl shadow-md text-white">
                  <div className="flex items-center gap-3">
                    {/* Baby image */}
                    <img
                      src="/images/usage_calc/baby_usage_calc.png"
                      alt="Baby Calculator"
                      className="w-15 h-15 rounded-full object-cover"
                    />
                    <div>
                      <h1 className="text-lg font-bold">{baby?.name || "Your Baby"}</h1>
                      <p className="text-white/80 text-lg">Usage Calculator</p>
                    </div>
                  </div>
                  <button
                    onClick={() => router.push('/usage-calculator/edit')}
                    className="bg-white text-blue-600 rounded-lg shadow px-3 py-1.5 hover:bg-white/90 transition-all font-semibold flex items-center gap-1.5 text-sm"
                  >
                    <svg className="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                      <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M11 5H6a2 2 0 00-2 2v11a2 2 0 002 2h11a2 2 0 002-2v-5m-1.414-9.414a2 2 0 112.828 2.828L11.828 15H9v-2.828l8.586-8.586z" />
                    </svg>
                    Edit
                  </button>
                </div>


                {/* Main Content */}
                <div className="grid grid-cols-1 lg:grid-cols-3 gap-4 pt-3">
                    {/* Baby Stats Grid - Takes 2 columns */}
                    <div className="lg:col-span-2">
                        <div className="grid grid-cols-2 md:grid-cols-3 gap-3">
                        {Object.entries(vars).map(([title, data]) => (
                            <div
                                key={title}
                                onClick={() => router.push('/usage-calculator/edit')}
                                className="group bg-blue-50 rounded-2xl shadow-md hover:shadow-lg transition-all duration-300 overflow-hidden hover:-translate-y-1 cursor-pointer"
                            >
                                {/* Image at top */}
                                <div className="w-full h-24 flex items-center justify-center overflow-hidden">
                                    <img
                                        src={data.image}
                                        alt={title}
                                        className="object-contain h-full"
                                    />
                                </div>

                                {/* Title bar */}
                                <div className="p-3 text-gray-800">
                                    <p className="font-medium text-lg">{title}</p>
                                </div>

                                {/* Value */}
                                <div className="p-3">
                                    <h2 className="text-2xl font-bold text-accent-primary">
                                        {data.value[0]}{data.value[1]}
                                    </h2>
                                </div>
                            </div>
                        ))}

                        </div>
                    </div>

                    {/* Diaper Status Widget */}
                    <div className="lg:col-span-1">
                    <div className="rounded-2xl shadow-lg p-4 bg-white">
                        <div className="flex items-center justify-between mb-4">
                            <h2 className="text-lg mr-4 font-bold text-gray-800">
                                Diaper Status
                            </h2>
                            {/* Status Badge */}
                            <span className={`px-4 py-1 rounded-full text-xs font-semibold ${
                                diaperLast > 10
                                    ? 'bg-green-100 text-green-700'
                                    : diaperLast >= 4
                                    ? 'bg-yellow-100 text-yellow-700'
                                    : 'bg-red-100 text-red-700'
                            }`}>
                                {diaperLast > 10 ? 'Plenty in Stock' : diaperLast >= 4 ? 'Running Low' : 'Restock Needed'}
                            </span>
                        </div>

                        {/* Days Remaining */}
                        <div className="bg-gray-50 rounded-xl p-2 mb-2 border border-gray-300">
                            <p className="text-gray-600 text-xs mb-1">Estimated Days Remaining</p>
                            <div className="flex items-end gap-2">
                                <span className={`text-4xl font-bold ${
                                    diaperLast > 10
                                        ? 'text-green-600'
                                        : diaperLast >= 4
                                        ? 'text-yellow-600'
                                        : 'text-red-600'
                                }`}>{diaperLast}</span>
                                <span className="text-xl mb-1 text-gray-600">days</span>
                            </div>
                            {/* Progress Bar */}
                            <div className="mt-4 bg-gray-200 rounded-full h-2 overflow-hidden">
                                <div
                                    className={`h-full rounded-full transition-all duration-500 ${
                                        diaperLast > 10
                                            ? 'bg-green-500'
                                            : diaperLast >= 4
                                            ? 'bg-yellow-500'
                                            : 'bg-red-500'
                                    }`}
                                    style={{ width: `${Math.min((diaperLast / 30) * 100, 100)}%` }}
                                />
                            </div>
                        </div>

                        {/* Purchase Recommendation */}
                        <div className="bg-gray-50 rounded-xl p-2 mb-2 border border-gray-300">
                            <p className="text-gray-600 text-xs mb-1">Suggested purchase amount</p>
                            <div className="flex items-center gap-2">
                                <span className="text-3xl font-bold text-gray-800">{diaperBoxes}</span>
                                <span className="text-base text-gray-600">boxes (minimum)</span>
                            </div>
                        </div>

                        {/* Message */}
                        {diaperUsage?.message && (
                            <div className="bg-blue-50 rounded-xl p-2 border border-gray-300">
                                <p className="text-xs text-blue-700">
                                    {diaperUsage.message}
                                </p>
                            </div>
                        )}
                    </div>
                    </div>
                </div>
            </div>
          </div>
        </div>
    );
}
