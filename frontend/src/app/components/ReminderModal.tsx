"use client";
import Image from "next/image";
import React, { useRef, useState, useEffect } from "react";

type RepeatOption =
  | "once"
  | "daily"
  | "weekly"
  | "monthly"
  | "quarterly"
  | "annual";

interface ReminderModalProps {
  taskName: string;
  setTaskName: (value: string) => void;
  taskDescription: string;
  setTaskDescription: (value: string) => void;
  repeat: RepeatOption | null;
  setRepeat: (value: RepeatOption) => void;
  date: string;
  setDate: (value: string) => void;
  minDate?: string;
  errorMessage?: string;
  modeType?: "task" | "vaccination" | null;
  setModeType?: (mode: "task" | "vaccination" | null) => void;
  onCancel: () => void;
  onSubmit: () => void;
  submitLabel?: string;
}

export default function ReminderModal({
  taskName,
  setTaskName,
  taskDescription,
  setTaskDescription,
  repeat,
  setRepeat,
  date,
  setDate,
  minDate,
  errorMessage,
  onCancel,
  onSubmit,
  setModeType,
  modeType,
  submitLabel = "Save",
}: ReminderModalProps) {
  const modalRef = useRef<HTMLDivElement>(null);

  // Start with no mode selected
  const [mode, setMode] = useState<"task" | "vaccination" | null>(modeType ?? null);
  const handleOverlayClick = (e: React.MouseEvent) => {
    if (modalRef.current && e.target === modalRef.current) onCancel();
  };

  // Images for the buttons / selection
  const selectionImages = {
    task: "/logo/other/reminder_no_task.png",
    vaccination: "/logo/other/reminder_no_vaccination.png",
  };

  return (
    <div
      ref={modalRef}
      onClick={handleOverlayClick}
      className="fixed inset-0 bg-black/40 flex justify-center items-center z-50"
    >
      <div className="bg-white p-6 md:p-8 rounded-2xl w-11/12 max-w-4xl shadow-lg relative">

        {/* NO MODE SELECTED → SHOW ONLY BUTTONS WITH IMAGE */}
        {mode === null && (
          <div className="flex flex-col items-center gap-4 w-full">
            {/* Instruction Text */}
            <p className="text-2xl font-bold text-accent-primary">Choose an action</p>

            <div className="flex w-full gap-3">
              <button
                onClick={() => {
                    setMode("task");
                    setModeType?.("task");
                    }}
                className={`flex-1 flex flex-col items-center justify-center rounded-[20px] shadow-md border transition-all duration-200 ease-in-out p-3 min-h-[120px] break-words overflow-hidden
                            bg-light text-accent-primary font-bold hover:bg-accent-primary/20`}
              >
                Add Task
                <Image
                  src={selectionImages.task}
                  alt="No Task Icon"
                  width={50}
                  height={50}
                  className="mt-1 object-contain"
                />
              </button>

              <button
                onClick={() => {
                    setMode("vaccination")
                    setModeType?.("vaccination");
                    setRepeat("once")
                    }}
                className={`flex-1 flex flex-col items-center justify-center rounded-[20px] shadow-md border transition-all duration-200 ease-in-out p-3 min-h-[120px] break-words overflow-hidden
                            bg-light text-accent-primary font-bold hover:bg-accent-primary/20`}
              >
                Add Vaccination
                <Image
                  src={selectionImages.vaccination}
                  alt="No Vaccination Icon"
                  width={50}
                  height={50}
                  className="mt-1 object-contain"
                />
              </button>
            </div>
          </div>
        )}

        {/* AFTER SELECTION → SHOW FORM */}
        {mode !== null && (
          <>
            {/* Title + Image */}
            <div className="flex items-center justify-between mb-4">
              <div className="flex-1 pr-4">
                <h2 className="text-3xl font-bold text-accent-primary">
                  {mode === "vaccination" ? "Vaccination" : "Task"}
                </h2>

                {errorMessage && (
                  <div className="mt-2 flex items-center gap-2 rounded-md bg-[var(--light-blue)] text-[var(--dark-blue)] px-3 py-2 text-sm shadow-sm">
                    <img
                      src="/logo/svg/warning.svg"
                      alt="Warning icon"
                      className="w-4 h-4 flex-shrink-0"
                    />
                    <span className="font-medium leading-snug">{errorMessage}</span>
                  </div>
                )}
              </div>

              <div className="w-16 h-16 md:w-20 md:h-20 relative flex-shrink-0">
                <Image
                  src={mode === "vaccination" ? selectionImages.vaccination : selectionImages.task}
                  alt="Task/Vaccination Icon"
                  fill
                  className="object-contain"
                  priority
                />
              </div>
            </div>

            {/* Task Name */}
            <div className="mb-4">
              <input
                type="text"
                value={taskName}
                onChange={(e) => setTaskName(e.target.value)}
                placeholder="Name"
                className="w-full border border-gray-300 rounded-lg px-4 py-3 text-lg"
              />
            </div>

            {/* Description */}
            <textarea
              value={taskDescription}
              onChange={(e) => setTaskDescription(e.target.value)}
              placeholder="Description"
              className="w-full border border-gray-300 rounded-lg px-4 py-3 mb-4 text-lg resize-none"
              rows={3}
            />

            {/* Repeat Options (hidden for vaccination) */}
            {mode !== "vaccination" && (
              <div className="flex flex-wrap gap-2 mb-4">
                {["once", "daily", "weekly", "monthly", "quarterly", "annual"].map(
                  (rep) => (
                    <button
                      key={rep}
                      onClick={() => setRepeat(rep as RepeatOption)}
                      className={`px-4 py-2 rounded-full text-sm font-medium transition ${
                        repeat === rep
                          ? "bg-accent-primary text-white shadow-md scale-105"
                          : "bg-gray-200 text-gray-700 hover:bg-gray-300"
                      }`}
                    >
                      {rep.charAt(0).toUpperCase() + rep.slice(1)}
                    </button>
                  )
                )}
              </div>
            )}

            {/* Date */}
            <div className="mb-4">
              <label className="block mb-1 text-sm font-medium text-gray-700">
                {mode === "vaccination" ? "Due Date" : "Next Due Date"}
              </label>
              <input
                type="date"
                value={date}
                onChange={(e) => setDate(e.target.value)}
                min={minDate}
                className="w-full border border-gray-300 rounded-lg px-4 py-2 text-lg"
              />
            </div>

            {/* Actions */}
            <div className="flex justify-end gap-4">
              <button
                onClick={onCancel}
                className="px-4 py-2 bg-black/15 ease-in-out duration-300 hover:bg-black/30 rounded-[30px]"
              >
                Cancel
              </button>
              <button
                onClick={onSubmit}
                className="px-4 py-2 bg-accent-primary ease-in-out duration-300 text-white hover:bg-secondary rounded-[30px]"
              >
                {submitLabel}
              </button>
            </div>
          </>
        )}
      </div>
    </div>
  );
}
