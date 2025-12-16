"use client";

import React, { useEffect, useMemo, useState } from "react";
import { suggestionTable, UsageSuggestion } from "@/constants/DiaperUsagePattern";
import { babyApi, diaperApi } from "@/services/baby-api";
import { usageCalculatorApi } from "@/services/usage-calculator";
import { useSession } from "next-auth/react";
import { useRouter } from "next/navigation";
import LoadingState from '@/app/product/components/LoadingState';
import ErrorState from '@/app/product/components/ErrorState';
import Link from "next/link";

/* ----------------- Helpers ----------------- */
function computeAgeMonths(dobIso: string | null | undefined): number {
    if (!dobIso) return 0;

    const dob = new Date(dobIso);
    const now = new Date();
    const years = now.getFullYear() - dob.getFullYear();
    let months = now.getMonth() - dob.getMonth() + years * 12;

    if (now.getDate() < dob.getDate()) months -= 1;

    return Math.max(0, months);
}

/**
 * Return object with current suggestion and neighbours (previous/next) based on weight and age.
 * Logic:
 *  - Prefer entries where weight falls within range.
 *  - If multiple matches, pick the one whose weight is closest to baby's weight.
 *  - If no exact match, pick the closest by weight distance.
 */
export function getSuggestedSizeFromWeightAndAge(
    weightKg: number | null,
    ageMonths: number | null
): { previous?: UsageSuggestion; current?: UsageSuggestion; next?: UsageSuggestion } | null {
    if (weightKg == null || isNaN(weightKg)) return null;

    const table = suggestionTable;

    // Step 1: filter entries where weight is in range
    const weightMatches = table.filter(entry => {
        const min = entry.weightMin ?? -Infinity;
        const max = entry.weightMax ?? Infinity;
        return weightKg >= min && weightKg <= max;
    });

    let matched: UsageSuggestion | null = null;

    if (weightMatches.length === 1) {
        matched = weightMatches[0];
    } else if (weightMatches.length > 1) {
        // Step 2: try to match age
        const ageFiltered = ageMonths != null
            ? weightMatches.filter(entry => {
                const minAge = entry.ageMinMonths ?? -Infinity;
                const maxAge = entry.ageMaxMonths ?? Infinity;
                return ageMonths >= minAge && ageMonths <= maxAge;
            })
            : [];

        if (ageFiltered.length > 0) {
            // pick closest weight among age matches
            matched = ageFiltered.reduce((best, entry) => {
                const midpoint = ((entry.weightMin ?? 0) + (entry.weightMax ?? 0)) / 2;
                const bestMid = ((best.weightMin ?? 0) + (best.weightMax ?? 0)) / 2;
                return Math.abs(weightKg - midpoint) < Math.abs(weightKg - bestMid) ? entry : best;
            });
        } else {
            // no age matches, fallback to closest weight
            matched = weightMatches.reduce((best, entry) => {
                const midpoint = ((entry.weightMin ?? 0) + (entry.weightMax ?? 0)) / 2;
                const bestMid = ((best.weightMin ?? 0) + (best.weightMax ?? 0)) / 2;
                return Math.abs(weightKg - midpoint) < Math.abs(weightKg - bestMid) ? entry : best;
            });
        }
    }

    // Step 3: if no weight match at all, pick closest by weight from full table
    if (!matched) {
        matched = table.reduce((best, entry) => {
            const midpoint = ((entry.weightMin ?? 0) + (entry.weightMax ?? 0)) / 2;
            const bestMid = ((best.weightMin ?? 0) + (best.weightMax ?? 0)) / 2;
            return Math.abs(weightKg - midpoint) < Math.abs(weightKg - bestMid) ? entry : best;
        });
    }

    const matchedIndex = table.indexOf(matched);
    const prev = matchedIndex - 1 >= 0 ? table[matchedIndex - 1] : undefined;
    const next = matchedIndex + 1 < table.length ? table[matchedIndex + 1] : undefined;

    return { previous: prev, current: matched, next: next };
}

/* ----------------- Input Components ----------------- */
type InputFieldProps = {
    label: string;
    type?: string;
    value: string;
    onChange: (value: string) => void;
    suggestion?: string | null;
    image?: string;
};

function InputField({ label, type = "text", value, onChange, suggestion, image }: InputFieldProps) {
    return (
        <div className="mb-4 flex items-start gap-3">

            {image && (
                <img
                    src={image}
                    className="object-contain h-[100px] w-[100px]"
                />
            )}

            <div className="flex-1">
                <label className="font-semibold text-accent-primary block text-lg">
                    {label}
                </label>

                <input
                    type={type}
                    value={value}
                    onChange={(event) => onChange(event.target.value)}
                    className="w-full mt-1 p-3 rounded-xl border border-gray-300"
                />

                {suggestion && (
                    <p className="text-xs text-accent-primary mt-1">{suggestion}</p>
                )}
            </div>
        </div>
    );
}


type SelectFieldProps = {
    label: string;
    value: string;
    onChange: (value: string) => void;
    options: string[];
    suggestion?: string | null;
    image?: string;
};

function SelectField({ label, value, onChange, options, suggestion, image }: SelectFieldProps) {
    return (
        <div className="mb-4 flex items-start gap-3">

            {/* IMAGE WRAPPER THAT MATCHES INPUT HEIGHT */}
            <div className="flex flex-col justify-start">
                {image && (
                    <img
                        src={image}
                        className="object-contain h-[100px] w-[100px]"
                    />
                )}
            </div>

            <div className="flex-1">
                <label className="font-semibold text-accent-primary block text-lg">
                    {label}
                </label>

                <select
                    value={value}
                    onChange={(event) => onChange(event.target.value)}
                    className="w-full mt-1 p-3 rounded-xl border border-gray-300"
                >
                    <option value="">Select</option>
                    {options.map((option) => (
                        <option key={option} value={option}>
                            {option}
                        </option>
                    ))}
                </select>

                {suggestion && (
                    <p className="text-xs text-accent-primary mt-1">{suggestion}</p>
                )}
            </div>
        </div>
    );
}

/* ----------------- Suggestion Row ----------------- */
function SuggestionRow({
    title,
    suggestion,
    highlight = false,
    animate = false,
}: {
    title: string;
    suggestion: UsageSuggestion;
    highlight?: boolean;
    animate?: boolean;
}) {
    return (
        <div
            className={`p-3 rounded-xl flex justify-between items-center
            ${highlight ? "bg-accent-primary/10" : "bg-white"}
            transform transition-all duration-500 ease-out
            ${animate ? "translate-y-0 opacity-100" : "translate-y-4 opacity-0"}`}
        >
        <span className="font-medium text-accent-primary">{title}</span>
            <div className="text-right text-gray-600 text-xs">
                <p>Size: {suggestion.size}</p>
                <p>Weight: {suggestion.weightRange}</p>
                <p>Usage: {suggestion.avgUsage}</p>
                <p>Age: {suggestion.ageRange}</p>
                <p>Per Box: {suggestion.perBox}</p>
            </div>
        </div>
    );
}

/* ----------------- Main Component ----------------- */
export default function EditCalculator() {
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState<string | null>(null);

    const { data: session } = useSession();
    const router = useRouter();
    const userId = session?.user?.id;

    const [babyId, setBabyId] = useState<number | null>(null);
    const [dob, setDob] = useState<string>("");
    const [weight, setWeight] = useState<string>("");
    const [diaperSize, setDiaperSize] = useState<string>("");
    const [dailyUsage, setDailyUsage] = useState<string>("");
    const [diapersPerBox, setDiapersPerBox] = useState<string>("");
    const [boxesAtHome, setBoxesAtHome] = useState<string>("");

    const [debouncedWeight, setDebouncedWeight] = useState(weight);
    const [animateSuggestion, setAnimateSuggestion] = useState(false);

    // debounce weight
    useEffect(() => {
        // if first load, set immediately
        if (debouncedWeight === "" && weight !== "") {
            setDebouncedWeight(weight);
            return;
        }

        const timer = setTimeout(() => setDebouncedWeight(weight), 500);
        return () => clearTimeout(timer);
    }, [weight]);

    useEffect(() => {
    if (!userId) return;

    const loadBaby = async () => {
        try {
            setLoading(true);
            const babyList = await babyApi.getByUserId(Number(userId));

            if (!babyList?.length) {
                setError('No baby found for this user.');
                return;
            }

            const firstBaby = babyList[0];
            const babyDetails = await babyApi.getByBabyId(firstBaby.id);

            setBabyId(firstBaby.id);
            setDob(babyDetails.dob.slice(0, 10));
            setWeight(String(babyDetails.weight ?? ""));
            setDiaperSize(babyDetails.diaperSize ?? "");
            setDailyUsage(String(babyDetails.dailyUsage ?? ""));

            // fetch inventory for diapers
            const inventory = await diaperApi.getUserInventory(Number(userId));
            const diaperItem = inventory.find(item => item.supplyName.toLowerCase() === 'diapers');
            setDiapersPerBox(String(diaperItem?.unitConversion ?? ""));
            setBoxesAtHome(String(diaperItem?.totalUnitQuantity ?? ""));

            setError(null);

        } catch (err) {
            console.error('Failed to load baby:', err);
            setError('Failed to load baby data. Please try again later.');
        } finally {
            setLoading(false);
        }
    };

    loadBaby();
    }, [userId]);

    const numericWeightKg: number | null = debouncedWeight ? Number(debouncedWeight) || null : null;
    const ageMonthsNumber: number | null = dob ? computeAgeMonths(dob) : null;

    const suggestionRows = useMemo(() => getSuggestedSizeFromWeightAndAge(numericWeightKg, ageMonthsNumber), [
        numericWeightKg,
        ageMonthsNumber,
    ]);

    const currentSuggestion = suggestionRows?.current ?? undefined;
    const previousSuggestion = suggestionRows?.previous ?? undefined;
    const nextSuggestion = suggestionRows?.next ?? undefined;

    useEffect(() => {
        if (currentSuggestion) {
        setAnimateSuggestion(false);
        const timer = setTimeout(() => setAnimateSuggestion(true), 50);
        return () => clearTimeout(timer);
        }
    }, [currentSuggestion]);

    const handleSave = async (): Promise<void> => {
        if (babyId === null) return;
        await usageCalculatorApi.update({
            id: babyId,
            dob,
            weight: Number(weight || 0),
            diaperSize,
            dailyUsage: Number(dailyUsage || 0),
            diapersPerBox: Number(diapersPerBox || 0),
            boxesAtHome: Number(boxesAtHome || 0),
        });

        router.push("/usage-calculator");
    };

    if (loading) return <LoadingState />;
    if (error) return <ErrorState message={error} />;

    return (
        <div className="dashboard-page-content layout">
            <div className="w-full bg-white rounded-[40px] shadow-md pt-2 pb-10 px-6 md:pt-2 md:pb-10 md:px-10">
                <div className="flex items-center gap-4">
                    <img
                        src="/images/usage_calc/edit_diaper_usage.png"
                        alt="Edit Usage Illustration"
                        className="w-30 h-30 object-contain"
                    />
                    <h1 className="text-4xl md:text-3xl font-bold text-accent-primary !text-4xl">
                        Edit Diaper Usage
                    </h1>
                </div>
                {/* ----------------- First Grid ----------------- */}
                <div className="grid grid-cols-1 md:grid-cols-2 gap-6 mb-3">
                    {/* Baby Info */}
                    <div>
                        <p className="text-accent-primary font-semibold mb-2 text-2xl">Baby's Information</p>
                        <div className="grid grid-cols-1 gap-4">
                            <InputField label="Date of Birth" type="date" value={dob} onChange={setDob} image="/images/usage_calc/baby_age.png"/>
                            <InputField label="Weight (kg)" value={weight} onChange={setWeight} image="/images/usage_calc/baby_weight.png"/>
                        </div>
                    </div>

                    {/* Diaper Suggestion */}
                    <div className="flex flex-col">
                        <p className="text-accent-primary font-semibold mb-2 text-2xl">Recommended Diaper Size</p>

                        {numericWeightKg == null || ageMonthsNumber == null ? (
                            <p className="text-sm text-gray-500">
                            Please enter baby's weight and date of birth to see a suggestion.
                            </p>
                        ) : (
                            <>
                            <div className="hidden md:block">
                                {previousSuggestion && (
                                <SuggestionRow
                                    title="Previous"
                                    suggestion={previousSuggestion}
                                    animate={animateSuggestion}
                                />
                                )}
                            </div>

                            {currentSuggestion && (
                                <SuggestionRow
                                title="Suggested"
                                suggestion={currentSuggestion}
                                highlight
                                animate={animateSuggestion}
                                />
                            )}

                            <div className="hidden md:block">
                                {nextSuggestion && (
                                <SuggestionRow
                                    title="Next"
                                    suggestion={nextSuggestion}
                                    animate={animateSuggestion}
                                />
                                )}
                            </div>
                            </>
                        )}
                    </div>
                </div>

                {/* ----------------- Second Grid ----------------- */}
                <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
                    {/* Usage Calculator */}
                    <div>
                        <p className="text-accent-primary font-semibold mb-2 text-2xl">Usage Information</p>
                        <SelectField
                            label="Diaper Size"
                            value={diaperSize}
                            onChange={setDiaperSize}
                            options={suggestionTable.map((s) => s.size)}
                            suggestion={currentSuggestion?.size && `Suggested: ${currentSuggestion.size}`}
                            image="/images/usage_calc/diaper_size.png"
                        />
                        <InputField
                            label="Average Diaper Usage Per Day"
                            value={dailyUsage}
                            onChange={setDailyUsage}
                            suggestion={currentSuggestion?.avgUsage && `Suggested: ${currentSuggestion.avgUsage}`}
                            image="/images/usage_calc/diaper_usage.png"
                        />
                        <InputField
                            label="Number of Diapers Per Box"
                            value={diapersPerBox}
                            onChange={setDiapersPerBox}
                            suggestion={currentSuggestion?.perBox && `Suggested: ${currentSuggestion.perBox}`}
                            image="/images/usage_calc/diaper_per_box.png"
                        />
                        <InputField
                            label="Number of Diaper Boxes at Home"
                            value={boxesAtHome}
                            onChange={setBoxesAtHome}
                            image="/images/usage_calc/diaper_at_home.png"
                        />
                    </div>

                    {/* Selected Size Info */}
                    <div className=" hidden md:flex flex-col gap-2">
                        <div className="hidden md:block h-min p-4 rounded-xl border border-gray-300
                                        bg-gradient-to-bl from-secondary/40 to-primary/50">
                            <p className="text-accent-secondary font-semibold mb-2 text-2xl">Your Selected Size</p>
                            {(() => {
                                const selected = suggestionTable.find((s) => s.size === diaperSize);
                                if (!selected) return <p className="text-medium text-gray-500">Unknown size</p>;
                                return (
                                    <div className="text-medium text-accent-secondary">
                                        <p>Size: {selected.size}</p>
                                        <p>Weight: {selected.weightRange}</p>
                                        <p>Age: {selected.ageRange}</p>
                                        <p>Usage/day: {selected.avgUsage}</p>
                                        <p>Per Box: {selected.perBox}</p>
                                    </div>
                                );

                            })()}
                        </div>

                        <div className="hidden md:block h-min p-4 rounded-xl border border-gray-300
                                        bg-gradient-to-bl from-secondary/40 to-primary/50">
                            <p className="text-medium text-accent-secondary">
                                Don't worry! We'll automatically update your <span className="font-bold">
                                    Number of Diaper Boxes at Home
                                </span> for you daily based on your <span className="font-bold">
                                    Average Diaper Usage per Day
                                </span>!
                            </p>
                        </div>

                        <Link
                            href="/price-comparison"
                            className="mt-1 px-1 py-2 bg-accent-primary text-white rounded-lg text-center hover:bg-accent-secondary transition-colors text-sm"
                            >
                            Browse Products
                        </Link>
                    </div>
                </div>

                {/* Submit Button */}
               <button
                   className="mt-5 w-full bg-accent-primary text-white py-3 px-4 rounded-2xl font-semibold"
                   onClick={handleSave}
               >
                   Save Changes
               </button>
            </div>
        </div>
    );
}