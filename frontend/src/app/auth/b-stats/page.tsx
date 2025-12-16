'use client';

import Image from "next/image";
import { useState } from "react";
import { useRouter } from "next/navigation";
import { useSession } from "next-auth/react";
import { babyApi } from "../../../services/baby-api"

export default function BabyStats() {
    const router = useRouter();
    const { data: session, status } = useSession();
    const [stats, setStats] = useState({ babyName: "", babyDOB: "", babyWeight: "" });

    const handleSubmit = async (e: React.FormEvent) => {
      e.preventDefault();

      if (!session?.user?.id) {
        alert("User session not ready");
        return;
      }

      try {
        const payload = {
          name: stats.babyName,
          dob: stats.babyDOB,
          weight: parseFloat(stats.babyWeight),
          userId: Number(session.user.id),
        };
        const createdBaby = await babyApi.create(payload);
        router.push("../dashboard");
      } catch (error) {
        alert("Something went wrong while saving baby info. Please try again.");
      }
    };


    return (
        <section className="min-h-screen flex items-center justify-evenly bg-gradient-to-b from-secondary to-accent-primary text-foreground px-6">
            <div className="max-w-md w-full bg-highlight rounded-2xl shadow-lg p-8 space-y-4">
                <div className="text-center">
                    <h2 className="font-bold text-accent-primary mb-1">
                        Thank you for registering with us!
                    </h2>
                    <p>Before we get started, we need a bit of extra information.</p>
                </div>

                <form onSubmit={handleSubmit} className="flex flex-col gap-2">
                    <label className="flex flex-col text-black/50">Baby's Name
                    <input
                        type="text"
                        placeholder="Baby's Name"
                        required
                        className="input"
                        value={stats.babyName}
                        onChange={(e) => setStats({ ...stats, babyName: e.target.value })}
                    /></label>

                    <label className="flex flex-col text-black/50">Baby's Date of Birth
                    <input
                        type="date"
                        required
                        className="input"
                        value={stats.babyDOB}
                        max={new Date().toISOString().split('T')[0]}
                        onChange={(e) => {
                            const today = new Date().toISOString().split('T')[0];

                            if (e.target.value > today) {
                                alert("Date of birth cannot be in the future!");
                                return;
                            }

                            setStats({ ...stats, babyDOB: e.target.value });
                        }}
                    /></label>

                    <label className="flex flex-col text-black/50">
                        Baby's Weight
                        <div className="flex items-center justify-between">
                            <input
                                type="number"
                                required
                                className="input w-full mr-2"
                                value={stats.babyWeight}
                                min={0.1}
                                step="0.1"
                                onChange={(e) => {
                                    const value = parseFloat(e.target.value);
                                    if (isNaN(value)) {
                                        setStats({ ...stats, babyWeight: '' });
                                        return;
                                    }
                                    if (value < 0) {
                                        alert("Weight cannot be negative!");
                                        return;
                                    }
                                    setStats({ ...stats, babyWeight: e.target.value });
                                }}
                            />
                            <span className="text-black/50 whitespace-nowrap">kgs</span>
                        </div>
                    </label>
                    {/* TODO: allow user to select kgs or lbs */}
                    {/* <label className="flex flex-col text-black/50">Unit
                    <select
                        id="unit"
                        className="input"
                        value={stats.weightUnit}
                    >
                        <option value='kgs'>kgs</option>
                        <option value='lbs' selected>lbs</option>
                    </select>
                    </label> */}

                    <button
                        type="submit"
                        className="bg-accent-primary text-white rounded-lg py-2 hover:bg-accent-secondary transition-colors text-center mt-5 mb-3"
                    >
                        Submit
                    </button>
                </form>
            </div>

            <Image
                src={"/logo/other/logo2_white.png"}
                alt={"Nestuity Logo 2"}
                width={600}
                height={600}
                className="hidden lg:block w-full h-auto max-w-[600px] object-contain"
            />
        </section>
    );
}