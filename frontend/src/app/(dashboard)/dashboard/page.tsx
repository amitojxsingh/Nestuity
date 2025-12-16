'use client';

import SearchBar from '../../components/SearchBar';
import Link from 'next/link';
import { useState, useEffect } from 'react';
import { babyReminderAPI } from '@/services/intel-reminder-api';
import { BabyReminder } from '@/types/reminder.types';
import { Baby } from '@/types/baby.types';
import {auth} from "@/auth";
import {useSession} from "next-auth/react";
import { useRouter } from "next/navigation";
import { babyApi } from "@/services/baby-api";
import { babyProductAPI } from '@/services/baby-product-api';
import type { BabyProduct, PriceHistoryItem } from '@/types/product.types';

type SkeletonLineProps = {
  width?: string | number;
  height?: string | number;
};

export default function Dashboard() {
    const router = useRouter();
    const {data: session, status} = useSession();
    const userId = session?.user?.id;
    const [baby, setBaby] = useState<Baby | null>(null);
    // --- Tasks ---
    const [tasksTab, setTasksTab] = useState<'daily' | 'weekly' | 'vaccinations'>('daily');
    const [tasks, setTasks] = useState<{ [key: string]: BabyReminder[] }>({
        daily: [],
        weekly: [],
        vaccinations: [],
    });
    const [loadingTasks, setLoadingTasks] = useState(true);
    const [tasksError, setTasksError] = useState<string | null>(null);

    // --- Milestone ---
    const [milestone, setMilestone] = useState<BabyReminder | null>(null);
    const [loadingMilestone, setLoadingMilestone] = useState(true);
    const [milestoneError, setMilestoneError] = useState<string | null>(null);
    
    // --- Recent Searches with Prices ---
    const [recentLines, setRecentLines] = useState<Array<{ term: string; line: string; productId?: number }>>([]);
    const [recentLoading, setRecentLoading] = useState<boolean>(false);
    const [recentError, setRecentError] = useState<string | null>(null);

    useEffect(() => {
        if (status === "loading") return;
        if (status === "unauthenticated" || !userId) {
            router.replace("/auth/login");
            return;
        }
        const fetchData = async () => {
            const babyIdFetched = await fetchBaby()
            if (babyIdFetched == null) { return; }
            await fetchMilestone(babyIdFetched);
            await fetchReminders(babyIdFetched);
        };
        fetchData();
    }, [userId, status]);

    useEffect(() => {
        const historyKey = 'nestuitySearchHistory';
        try {
            const historyRaw = typeof window !== 'undefined' ? localStorage.getItem(historyKey) : null;
            const history: Array<{ term: string; ts: number }> = historyRaw ? JSON.parse(historyRaw) : [];
            const seen = new Set<string>();
            const recentCandidates: string[] = [];
            for (let i = history.length - 1; i >= 0 && recentCandidates.length < 15; i--) {
                const t = history[i].term.trim();
                if (!t) continue;
                if (!seen.has(t)) {
                    seen.add(t);
                    recentCandidates.push(t);
                }
            }

            if (recentCandidates.length === 0) {
                setRecentLines([]);
                return;
            }

            const latestByRetailer = (hist: PriceHistoryItem[]) => {
                const map = new Map<string, PriceHistoryItem>();
                for (const item of hist) {
                    const prev = map.get(item.retailer);
                    if (!prev || new Date(item.date).getTime() > new Date(prev.date).getTime()) {
                        map.set(item.retailer, item);
                    }
                }
                return Array.from(map.values());
            };

            const run = async () => {
                setRecentLoading(true);
                setRecentError(null);
                try {
                    const products: BabyProduct[] = await babyProductAPI.getAll();
                    const allOffers = products.flatMap((p) => {
                        if (!p.priceHistory || p.priceHistory.length === 0) return [] as Array<{
                            productId: number;
                            name: string;
                            brand: string;
                            category: string;
                            retailer: string;
                            price: number;
                        }>;
                        return latestByRetailer(p.priceHistory).map((h) => ({
                            productId: p.id,
                            name: p.name,
                            brand: p.brand,
                            category: p.category,
                            retailer: h.retailer,
                            price: h.price,
                        }));
                    });
                    const lines: Array<{ term: string; line: string; productId: number }> = [];
                    for (const term of recentCandidates) {
                        if (lines.length >= 3) break;
                        const t = term.toLowerCase();
                        const offers = allOffers.filter((o) =>
                            (o.name?.toLowerCase().includes(t)) ||
                            (o.brand?.toLowerCase().includes(t)) ||
                            (o.category?.toLowerCase().includes(t)) ||
                            (o.retailer?.toLowerCase().includes(t))
                        );
                        if (offers.length === 0) continue; 

                        const minPrice = offers.reduce((m, o) => Math.min(m, o.price), offers[0].price);
                        const cheapestOffers = offers.filter((o) => o.price === minPrice);
                        const byProduct = new Map<number, { name: string; retailers: Set<string> }>();
                        for (const o of cheapestOffers) {
                            if (!byProduct.has(o.productId)) {
                                byProduct.set(o.productId, { name: o.name, retailers: new Set([o.retailer]) });
                            } else {
                                byProduct.get(o.productId)!.retailers.add(o.retailer);
                            }
                        }
                        const [chosenProductId, info] = Array.from(byProduct.entries()).sort((a, b) => b[1].retailers.size - a[1].retailers.size)[0];
                        const retailersList = Array.from(info.retailers).sort();
                        const line = `${info.name} — ${retailersList.join(', ')}: $${minPrice.toFixed(2)}`;
                        lines.push({ term, line, productId: chosenProductId });
                    }

                    setRecentLines(lines);
                } catch (err) {
                    setRecentError('Unable to load recent prices right now.');
                    setRecentLines([]);
                } finally {
                    setRecentLoading(false);
                }
            };

            run();
        } catch (_) {
            // ignore malformed storage
        }
    }, []);

    const fetchBaby = async () => {
        try {
            const babies: Baby[] = await babyApi.getByUserId(Number(userId));
            if (!babies || babies.length === 0) {
                return null;
            }
        const firstBaby = babies[0];
        setBaby(firstBaby)
        return firstBaby.id;
        } catch (err) {
            return null;
        }
    };

    const fetchReminders = async (babyIdFetched: number) => {
        setLoadingTasks(true);
        setTasksError(null);

        try {
            let reminders = await babyReminderAPI.getUpcoming(babyIdFetched, 7);
            reminders = reminders.filter(reminder => reminder.range !== "COMPLETED");
            const categorized: { [key: string]: BabyReminder[] } = { daily: [], weekly: [], vaccinations: [] };

            reminders.forEach(r => {
                const category = categorizeReminder(r);
                if (category) categorized[category].push(r);
            });

            setTasks(categorized);
        } catch (err) {
            console.error('Failed to fetch reminders:', err);
            setTasksError('Failed to load tasks. Please try again later.');
        } finally {
            setLoadingTasks(false);
        }
    };

    const fetchMilestone = async (babyIdFetched: number) => {
        setLoadingMilestone(true);
        setMilestoneError(null);

        try {
            const milestone = await babyReminderAPI.getCurrentMilestone(babyIdFetched);
            setMilestone(milestone);
        } catch (err) {
            console.error('Error fetching milestone:', err);
            setMilestoneError('Failed to load milestone. Please try again later.');
        } finally {
            setLoadingMilestone(false);
        }
    };

    const categorizeReminder = (r: BabyReminder) => {
        if (r.type === 'TASK') return r.range === 'TODAY' ? 'daily' : 'weekly';
        if (r.type === 'VACCINATION') return 'vaccinations';
        return null;
    };

    const getBabyAgeInMonths = (dob: string | null | undefined) => {
        if (!dob) return '– months';
        const birthDate = new Date(dob);
        const now = new Date();

        const yearsDiff = now.getFullYear() - birthDate.getFullYear();
        const monthsDiff = now.getMonth() - birthDate.getMonth();
        const totalMonths = yearsDiff * 12 + monthsDiff;

        return `${totalMonths} months`;
    };

    // Skeleton component for a single line/item
    const SkeletonLine = ({ width = 'full', height = 6 }: SkeletonLineProps) => (
      <div
        className="bg-gray-300 rounded-md animate-pulse my-1"
        style={{
          width: typeof width === 'number' ? `${width}px` : width,
          height: typeof height === 'number' ? `${height}px` : height,
        }}
      ></div>
    );

    const renderTasks = (list: BabyReminder[]) => {
        if (loadingTasks) {
            // Show 3 skeleton items to simulate tasks
            return (
                <div className="mt-2">
                    <SkeletonLine />
                    <SkeletonLine />
                    <SkeletonLine />
                </div>
            );
        }
        if (tasksError) return <p className="text-red-500 mt-2">{tasksError}</p>;
        if (!list || list.length === 0) return <p className="text-gray-500 mt-2">Nothing here! Enjoy some rest 😄</p>;

        return (
            <ul className="mt-2 space-y-2 text-black">
                {list.map((task) => (
                    <li
                        key={task.id}
                        className="rounded-lg border border-light-grey bg-primary/20 px-3 py-2 shadow-sm hover:bg-secondary/20 transition text-lg"
                    >
                        {task.title}
                    </li>
                ))}
            </ul>
        );
    };

    const renderMilestone = () => {
        if (loadingMilestone) {
            // Skeletons for title + description
            return (
                <div className="mt-2">
                    <SkeletonLine width="50%" height={24} />  {/* title */}
                    <SkeletonLine width="full" height={16} /> {/* description line 1 */}
                    <SkeletonLine width="full" height={16} /> {/* description line 2 */}
                </div>
            );
        }
        if (milestoneError) return <p className="text-red-500 mt-2">{milestoneError}</p>;
        if (!milestone?.description) return <p className="text-gray-500 mt-2">Take care and enjoy these early days — milestones will appear soon.</p>;

        return (
            <>
                <h2 className="text-accent-secondary">{milestone.title}</h2>
                <p className="flex-1 text-base md:text-lg">{milestone.description}</p>
            </>
        );
    };

    const handleProductClick = (productId: number) => {
        router.push(`/product/${productId}`);
    };

    return (
        <>
            <div className='flex flex-1 layout h-full py-3'>
                <div className='flex flex-col md:grid md:grid-rows-5 md:grid-cols-5 md:h-full gap-8 md:gap-10 w-full'>
                    {/* Intro and Price Analysis */}
                    <div className='order-1 flex flex-col justify-between gap-8 w-full md:row-span-3 md:col-span-3'>
                        {/* Intro */}
                        <div className="flex flex-col justify-center md:justify-end space-y-1">
                            {/* Greeting line */}
                            <p className="text-3xl sm:text-4xl md:text-3xl text-black/60 font-medium drop-shadow-sm">
                                Hello{' '}
                                <b className="text-accent-primary md:hidden">
                                    {baby?.name || 'Baby'}
                                </b>
                            </p>

                            {/* Baby name and age */}
                            <p className="text-3xl sm:text-4xl font-bold drop-shadow-xl leading-tight -translate-y-1">
                                <span className="hidden md:inline">
                                    <b className="text-accent-primary">{baby?.name || 'Baby'}</b>{' '}
                                    <span className="font-normal text-black/60">is</span>{' '}
                                </span>
                                <b className="text-accent-primary whitespace-nowrap">
                                    {getBabyAgeInMonths(baby?.dob)}
                                </b>
                            </p>
                        </div>

                    {/* Price Analysis */}
                     <div className='widget flex flex-col flex-1 gap-2 justify-between'>
                        <div className='space-y-2'>
                            <SearchBar />
                            <div className='mt-2'>
                                {/* Header */}
                                <h1 className='text-[#0543BF] border-b border-[#D7E8FF] pb-1 font-medium'>
                                    Recent searches
                                </h1>
                                
                                {/* Loading State */}
                                {recentLoading && (
                                    <div className='mt-2 p-2 bg-[#F8F9F1] rounded border border-[#D7E8FF]'>
                                        <p className='text-[#6F0094]'>Loading recent prices…</p>
                                    </div>
                                )}
                                
                                {/* Error State */}
                                {!recentLoading && recentError && (
                                    <div className='mt-2 p-2 bg-[#F8F9F1] rounded border border-[#B19F44]'>
                                        <p className='text-[#6F0094]'>{recentError}</p>
                                    </div>
                                )}
                                
                                {/* Empty State */}
                                {!recentLoading && !recentError && recentLines.length === 0 && (
                                    <div className='mt-2 p-3 bg-[#F8F9F1] rounded border border-[#D7E8FF]'>
                                        <p className='text-[#0543BF] text-center'>Search to see recent prices.</p>
                                    </div>
                                )}
                                
                                {/* Results List */}
                                {!recentLoading && !recentError && recentLines.length > 0 && (
                                    <div className='mt-2 space-y-3'>
                                        {recentLines.map((r, index) => {
                                            const isClickable = typeof r.productId === 'number';
                                            return (
                                                <div
                                                    key={r.term}
                                                    onClick={isClickable ? () => handleProductClick(r.productId as number) : undefined}
                                                    onKeyDown={(e) => {
                                                        if (!isClickable) return;
                                                        if (e.key === 'Enter' || e.key === ' ') handleProductClick(r.productId as number);
                                                    }}
                                                    role={isClickable ? 'button' : undefined}
                                                    tabIndex={isClickable ? 0 : -1}
                                                    className={`
                                                        group relative rounded-xl border overflow-hidden transition-all duration-300
                                                        ${index % 4 === 0 ? 'bg-[#F8F9F1] border-[#D7E8FF]' :
                                                        index % 4 === 1 ? 'bg-white border-[#D7E8FF]' :
                                                        index % 4 === 2 ? 'bg-[#F8F9F1] border-[#D7E8FF]' :
                                                        'bg-white border-[#D7E8FF]'}
                                                        ${isClickable ? 'cursor-pointer hover:shadow-2xl hover:border-[#0543BF] hover:-translate-y-1' : 'cursor-default opacity-90'}
                                                    `}
                                                >
                                                    <div className="p-1">
                                                        <p className='text-[#231F20] font-medium mb-1 group-hover:text-[#0543BF] transition-colors duration-300 line-clamp-2'>
                                                            {r.line.split(' — ')[0]}
                                                        </p>
                                                        <div className='flex justify-between items-center mt-2 pt-1 border-t border-gray-100'>
                                                            <span className='text-[#0543BF] text-lg font-medium group-hover:text-[#6F0094] transition-colors duration-300'>
                                                                {r.line.split(' — ')[1]?.split(': ')[0]}
                                                            </span>
                                                            <span className='text-[#6F0094] font-bold text-lg group-hover:text-[#0543BF] transition-colors duration-300'>
                                                                {r.line.split(': ')[1]}
                                                            </span>
                                                        </div>
                                                    </div>
                                                    <div className="absolute inset-0 bg-gradient-to-t from-[#0543BF]/5 to-transparent opacity-0 group-hover:opacity-100 transition-opacity duration-300 pointer-events-none"></div>
                                                    <div className="absolute bottom-0 left-0 right-0 h-1 bg-gradient-to-r from-[#0543BF] to-[#6F0094] transform scale-x-0 group-hover:scale-x-100 transition-transform duration-300"></div>
                                                </div>
                                            );
                                        })}
                                    </div>
                                )}
                                </div>
                            </div>
                        </div>
                    </div>

                    {/* Usage Calculator */}
                    <div className='widget order-2 md:order-3 md:row-span-2 md:col-span-2 flex flex-col'>
                        <h1 className='text-accent-primary w-full border-b'>Usage Calculator</h1>
                        <div className='flex flex-col flex-1 gap-0.5 mb-12 mt-10 items-center'>
                            <h2 className='text-3xl'>Remaining Diapers:</h2>
                            <div className='flex gap-1'>
                                <img src='/icons/svg/diaper.svg' alt='diaper' width={35} height={35}/>
                                <h1 className='text-accent-secondary'>10 days</h1>
                            </div>
                            
                            <Link href='/usage-calculator' className='w-2/3 mt-1 text-center font-semibold text-2xl py-1.5 px-2 bg-accent-primary text-white hover:bg-secondary/50 hover:text-accent-primary ease-in-out duration-300 rounded-[20px] shadow-md'>Update Stock</Link>
                        </div>
                    </div>

                    {/* Intelligent Reminders */}
                    <div className='widget flex flex-col order-4 md:order-2 md:row-span-3 md:col-span-2 overflow-auto'>
                        <h1 className='text-accent-primary border-b'>Upcoming Tasks</h1>
                        {/* Tabs */}
                        {/* Tabs */}
                        <div className="flex justify-between border-b border-grey/50">
                            {["daily", "weekly", "vaccinations"].map(tab => {
                                // Map internal tab to user-friendly label
                                const labelMap: { [key: string]: string } = {
                                    daily: "Today",
                                    weekly: "Upcoming Week",
                                    vaccinations: "Vaccinations",
                                };
                                return (
                                    <button
                                        key={tab}
                                        onClick={() => setTasksTab(tab as any)}
                                        className={`flex-1 py-2 text-lg font-medium capitalize ${
                                            tasksTab === tab
                                                ? "text-accent-secondary border-b-2 border-accent-secondary"
                                                : "text-black hover:text-accent-secondary"
                                        }`}
                                    >
                                        {labelMap[tab]}
                                    </button>
                                );
                            })}
                        </div>
                        {/* Tasks */}
                        <div className='flex-1 overflow-auto mx-2 max-h-[320px]'>
                            {tasksTab === "daily" && renderTasks(tasks.daily)}
                            {tasksTab === "weekly" && renderTasks(tasks.weekly)}
                            {tasksTab === "vaccinations" && renderTasks(tasks.vaccinations)}
                        </div>
                    </div>

                    {/* Developmental Milestones */}
                    <div className="widget order-4 flex flex-col md:row-span-2 md:col-span-3">
                        <h1 className="text-accent-primary border-b">Baby Wonder Week</h1>
                        <div className="bg-gradient-to-r from-primary/40 to-secondary/30 max-h-2xl rounded-[30px] flex-1 mx-1.5 my-2 px-3 pt-1 pb-2 shadow-md">
                            {renderMilestone()}
                        </div>
                    </div>
                </div>
            </div>
        </>
    );
}