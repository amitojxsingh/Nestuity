"use client";
import Image from "next/image";
import { useState, useEffect, useCallback, use } from "react";
import {
  BabyReminder,
  Frequency,
  ReminderRange,
  ReminderType,
} from "@/types/reminder.types";
import { babyApi } from "@/services/baby-api";
import { babyReminderAPI } from "@/services/intel-reminder-api";
import LoadingState from "@/app/product/components/LoadingState";
import ReminderModal from "../../components/ReminderModal";
import { useRouter } from "next/navigation";
import { useSession } from "next-auth/react";

type AddTaskCategory = "tasks" | "milestones" | "vaccinations" | "overdue";
type RepeatOption =
  | "once"
  | "daily"
  | "weekly"
  | "monthly"
  | "quarterly"
  | "annual";

export default function IntelligenceReminder() {
  const router = useRouter();
  const { data: session, status } = useSession();
  const userId = session?.user?.id;
  const [babyId, setBabyId] = useState<number | null>(null);
  const [loading, setLoading] = useState(true);
  // raw reminders from backend
  const [reminders, setReminders] = useState<BabyReminder[] | null>(null);
  // separate current milestone widget
  const [babyWonderWeek, setBabyWonderWeek] = useState<BabyReminder | null>(
    null,
  );
  // UI filters
  const [categoryFilter, setCategoryFilter] = useState<
    "all" | "completed" | "vaccinations" | "tasks" | "overdue"
  >("all");
  const [timeFilter, setTimeFilter] = useState<
    "all" | "daily" | "weekly" | "monthly"
  >("all");
  // date label (client-only to prevent SSR hydration mismatch)
  const [dateLabel, setDateLabel] = useState<string>("All Dates");
  const tomorrow = new Date(
    Date.now() - new Date().getTimezoneOffset() * 60000,
  );
  tomorrow.setDate(tomorrow.getDate() + 1);
  const tomorrowStr = tomorrow.toISOString().split("T")[0];
  // add modal state
  const [addModelErrorMessage, setAddModelErrorMessage] = useState("");
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [newTaskName, setNewTaskName] = useState("");
  const [newTaskDescription, setNewTaskDescription] = useState("");
  const [newTaskStartDate, setNewTaskStartDate] = useState<string>("");
  const [addModeType, setAddModeType] = useState<"task" | "vaccination" | null>(null);
  const [newTaskRepeat, setNewTaskRepeat] = useState<RepeatOption | null>(null);
  // edit modal state
  const [editModelErrorMessage, setEditModelErrorMessage] = useState("");
  const [isEditModalOpen, setIsEditModalOpen] = useState(false);
  const [editingReminder, setEditingReminder] = useState<BabyReminder | null>(
    null,
  );
  const [editedTitle, setEditedTitle] = useState("");
  const [editTaskDescription, setEditTaskDescription] = useState("");
  const [nextDueDate, setNextDueDate] = useState<string>("");
  const [editedRepeat, setEditedRepeat] = useState<RepeatOption | null>(null);
  // fetch current milestone widget
  const fetchBaby = async () => {
    try {
      const babies = await babyApi.getByUserId(Number(userId));
      if (!babies || babies.length === 0) {
        return null;
      }
      const firstBaby = babies[0];
      setBabyId(firstBaby.id);
      return firstBaby.id;
    } catch (err) {
      return null;
    }
  };
  // ALWAYS call getUpcoming(babyId) with NO param (per your requirement)
  const fetchUpcomingReminders = async () => {
    if (babyId == null) { return; }
    try {
      const upcoming = await babyReminderAPI.getUpcoming(babyId); // no daysAhead
      setReminders(upcoming);
    } catch (err) {
      console.error("Error fetching reminders:", err);
    }
  };
  // initial load
  const fetchAll = useCallback(async () => {
    try {
      setLoading(true);
      if (!userId) {
        router.replace("/auth/login");
        return;
      }
      const babyIdFetched = await fetchBaby();
      if (babyIdFetched == null) { return; }
      const [milestone, upcoming] = await Promise.all([
        babyReminderAPI.getCurrentMilestone(babyIdFetched),
        babyReminderAPI.getUpcoming(babyIdFetched),
      ]);
      setBabyWonderWeek(milestone);
      setReminders(upcoming);
    } catch (err) {
    } finally {
      setLoading(false);
    }
  }, [userId]);

  useEffect(() => {
    if (status === "loading") return; // wait for session load
    if (status === "unauthenticated" || !userId) {
      router.replace("/auth/login");
      return;
    }
    fetchAll();
  }, [fetchAll, userId, status]);

  // keep the date label client-only to avoid SSR/client mismatch
  useEffect(() => {
    const today = new Date();
    const normalizeDate = (d: Date) =>
      new Date(d.getFullYear(), d.getMonth(), d.getDate());
    const start = normalizeDate(today);

    // Weekly range: tomorrow → today + 7
    const weekStart = new Date(start);
    weekStart.setDate(start.getDate() + 1); // day after today
    const weekEnd = new Date(start);
    weekEnd.setDate(start.getDate() + 7);

    // Monthly range: day after weekEnd → today + 30
    const monthStart = new Date(weekEnd);
    monthStart.setDate(weekEnd.getDate() + 1);
    const monthEnd = new Date(start);
    monthEnd.setDate(start.getDate() + 30);

    const options: Intl.DateTimeFormatOptions = {
      month: "long",
      day: "numeric",
      year: "numeric",
    };

    switch (timeFilter) {
      case "daily":
        setDateLabel(`${start.toLocaleDateString(undefined, options)}`); // today only
        break;
      case "weekly":
        setDateLabel(
          `${weekStart.toLocaleDateString(undefined, options)} – ${weekEnd.toLocaleDateString(undefined, options)}`,
        );
        break;
      case "monthly":
        setDateLabel(
          `${monthStart.toLocaleDateString(undefined, options)} – ${monthEnd.toLocaleDateString(undefined, options)}`,
        );
        break;
      default:
        // All dates: today → 30 days
        setDateLabel(
          `${start.toLocaleDateString(undefined, options)} – ${monthEnd.toLocaleDateString(undefined, options)}`,
        );
    }
  }, [timeFilter]);
  const noItemImages: Record<string, string> = {
    tasks: "/logo/other/reminder_no_task.png",
    vaccinations: "/logo/other/reminder_no_vaccination.png",
    completed: "/logo/other/reminder_no_completed.png",
    overdue: "/logo/other/reminder_no_overdue.png",
    default: "/logo/other/reminder_all.png", // fallback
  };
  /**
   * SIMPLIFIED CLIENT-SIDE FILTERING:
   * - Use backend-provided `range` and `type` (and completedOn) rather than inferring category from frequency.
   * - We include near-term ranges for 'all' (TODAY, UPCOMING_WEEK, UPCOMING_MONTH) plus OVERDUE, and exclude FUTURE.
   * - Completed reminders (range === COMPLETED or completedOn present) always go into the completed bucket.
   */
  const getAllowedRangesForView = () => {
    switch (timeFilter) {
      case "daily":
        return new Set<string>([ReminderRange.TODAY]);
      case "weekly":
        return new Set<string>([ReminderRange.UPCOMING_WEEK]);
      case "monthly":
        return new Set<string>([ReminderRange.UPCOMING_MONTH]);
      default:
        // 'all' — include near-term + overdue (but not FUTURE by default)
        return new Set<string>([
          ReminderRange.TODAY,
          ReminderRange.UPCOMING_WEEK,
          ReminderRange.UPCOMING_MONTH,
          ReminderRange.OVERDUE,
        ]);
    }
  };

  const allowedRanges = getAllowedRangesForView();
  // buckets keyed by UI category names
  const buckets: Record<
    "tasks" | "vaccinations" | "completed" | "overdue",
    BabyReminder[]
  > = {
    tasks: [],
    vaccinations: [],
    completed: [],
    overdue: [],
  };
  if (reminders) {
    for (const reminder of reminders) {
      const isRecurring = [
        Frequency.DAILY,
        Frequency.WEEKLY,
        Frequency.MONTHLY,
        Frequency.QUARTERLY,
        Frequency.ANNUAL,
      ].includes(reminder.frequency);
      // Only truly completed reminders go to 'completed'
      if (String(reminder.range) === ReminderRange.COMPLETED) {
        buckets.completed.push(reminder);
        continue;
      }
      if (reminder.range === ReminderRange.OVERDUE) {
        buckets.overdue.push(reminder);
        continue;
      }
      // Skip tasks outside the allowed ranges for the current filter
      if (!allowedRanges.has(String(reminder.range))) {
        continue;
      }
      // Bucket by backend-provided type
      switch (String(reminder.type)) {
        case ReminderType.VACCINATION:
          buckets.vaccinations.push(reminder);
          break;
        case ReminderType.TASK:
        default:
          buckets.tasks.push(reminder);
      }
    }
  }

  // Derived visible entries with category/time filters applied
  const visibleCategoryEntries = Object.entries(buckets).filter(
    ([category, arr]) => {
      if (categoryFilter !== "all") {
        // categoryFilter 'tasks' should show only tasks, not daily/weekly/monthly buckets
        return categoryFilter === category;
      }
      return true;
    },
  );
  // Actions (all re-fetch calls use no-arg fetchUpcomingReminders)
  const handleToggleComplete = async (r: BabyReminder) => {
    try {
      if (r.type === ReminderType.TASK) {
        await babyReminderAPI.completeTask(r.id);
      } else {
        await babyReminderAPI.complete(r.id);
      }
      await fetchUpcomingReminders();
    } catch (err) {
      console.error("Error toggling complete:", err);
    }
  };
  const handleDelete = async (r: BabyReminder) => {
    if (r.type !== ReminderType.TASK && r.type !== ReminderType.VACCINATION) {
      console.warn(
        "Only TASK AND VACCINATION reminders can be permanently deleted via API.delete()",
      );
      return;
    }
    try {
      await babyReminderAPI.delete(r.id);
      await fetchUpcomingReminders();
    } catch (err) {
      console.error("Error deleting reminder:", err);
    }
  };
  const openEditModal = (r: BabyReminder) => {
    setEditingReminder(r);
    setEditedTitle(r.title);
    setEditTaskDescription(r.description ?? "");
    setNextDueDate(
      r.nextDue ? new Date(r.nextDue).toISOString().split("T")[0] : "",
    );
    let repeatValue: RepeatOption | null;
    switch (r.frequency) {
      case Frequency.ONCE:
        repeatValue = "once";
        break;
      case Frequency.DAILY:
        repeatValue = "daily";
        break;
      case Frequency.WEEKLY:
        repeatValue = "weekly";
        break;
      case Frequency.MONTHLY:
        repeatValue = "monthly";
        break;
      case Frequency.QUARTERLY:
        repeatValue = "quarterly";
        break;
      case Frequency.ANNUAL:
        repeatValue = "annual";
        break;
      default:
        repeatValue = null;
    }
    setEditedRepeat(repeatValue);
    setIsEditModalOpen(true);
  };
  const handleSaveEdit = async () => {
    if (!editingReminder) return;
    const missingFields: string[] = [];

    if (!editedTitle.trim()) missingFields.push("task name");
    if (!nextDueDate && editedRepeat !== "daily")
      missingFields.push("next due date");
    if (!editedRepeat) missingFields.push("repeat frequency");

    if (missingFields.length > 0) {
      const message = `Please enter ${missingFields
        .join(", ")
        .replace(/, ([^,]*)$/, " and $1")}.`;
      setEditModelErrorMessage(message);
      return;
    }

    // Clear any previous error before continuing
    setEditModelErrorMessage("");

    try {
      const frequency =
        Frequency[editedRepeat?.toUpperCase() as keyof typeof Frequency];
      // Map frequency → occurrence in days
      const occurrenceMap: Record<Frequency, number> = {
        [Frequency.ONCE]: 0,
        [Frequency.DAILY]: 1,
        [Frequency.WEEKLY]: 7,
        [Frequency.MONTHLY]: 30,
        [Frequency.QUARTERLY]: 90,
        [Frequency.ANNUAL]: 365,
      };
      const payload: Partial<BabyReminder> = {
        title: editedTitle.trim(),
        frequency: frequency,
        occurrence: occurrenceMap[frequency],
        userCreated: true,
        startDate: nextDueDate ? new Date(nextDueDate).toISOString() : null,
        description: editTaskDescription,
      };
      await babyReminderAPI.update(editingReminder.id, payload);
      setIsEditModalOpen(false);
      setEditingReminder(null);
      await fetchUpcomingReminders();
    } catch (err) {
      console.error("Error saving edit:", err);
      setEditModelErrorMessage("Failed to edit task. Please try again.");
    }
  };
  const handleAddTask = async () => {
    const missingFields: string[] = [];

    if (!newTaskName.trim()) missingFields.push("name");
    if (!newTaskStartDate && newTaskRepeat !== "daily")
      missingFields.push("date");
    if (!newTaskRepeat) missingFields.push("repeat frequency");

    if (missingFields.length > 0) {
      const message = `Please enter ${missingFields
        .join(", ")
        .replace(/, ([^,]*)$/, " and $1")}.`;
      setAddModelErrorMessage(message);
      return;
    }

    // Clear any previous error before continuing
    setAddModelErrorMessage("");
    const frequency: Frequency =
      Frequency[newTaskRepeat?.toUpperCase() as keyof typeof Frequency] ??
      Frequency.ANNUAL;
    // Map frequency → occurrence in days
    const occurrenceMap: Record<Frequency, number> = {
      [Frequency.ONCE]: 0,
      [Frequency.DAILY]: 1,
      [Frequency.WEEKLY]: 7,
      [Frequency.MONTHLY]: 30,
      [Frequency.QUARTERLY]: 90,
      [Frequency.ANNUAL]: 365,
    };
    if (babyId == null) { return; }
    const payload: Partial<BabyReminder> = {
      babyId: babyId,
      type: addModeType == "task" ? ReminderType.TASK : ReminderType.VACCINATION,
      title: newTaskName.trim(),
      description: newTaskDescription,
      frequency: frequency,
      occurrence: occurrenceMap[frequency],
      userCreated: true,
      startDate: newTaskStartDate
        ? new Date(newTaskStartDate).toISOString()
        : null,
    };
    try {
      await babyReminderAPI.createTask(babyId, payload);
      setNewTaskName("");
      setNewTaskDescription("");
      setNewTaskStartDate("");
      setNewTaskRepeat(null);
      setIsModalOpen(false);
      await fetchUpcomingReminders();
    } catch (err) {
      console.error("Error creating task:", err);
      setAddModelErrorMessage("Failed to create task. Please try again.");
    }
  };
  const handleCancel = () => {
    setIsModalOpen(false);
    setAddModelErrorMessage("");
    setNewTaskName("");
    setNewTaskDescription("");
    setNewTaskStartDate("");
    setNewTaskRepeat(null);
  };

  if (loading) {
    return <LoadingState />;
  }
  return (
    <div className="w-full">
      <div className="flex flex-col h-full items-center gap-15 my-8 md:my-0 mx-5">
        <div className="widget w-full flex flex-col mt-3 mb-4 min-h-[82vh] p-4">
          {" "}
          {/* Wonder Week Widget */}
          <div className="w-full px-3 relative">
            <div className="w-full bg-gradient-to-bl from-secondary/40 to-primary/50 shadow-md rounded-xl flex flex-col md:flex-row items-center gap-2 overflow-hidden">
              {/* Image section */}
              <div className="flex justify-center items-center w-full md:w-1/5 md:p-3">
                <div className="relative w-18 h-18 md:w-28 md:h-28">
                  <Image
                    src="/logo/other/reminder_milestone.png"
                    alt="Nestuity Logo"
                    fill
                    className="object-contain"
                    priority
                  />
                </div>
              </div>
              {/* Text section */}
              <div className="flex flex-col justify-center text-center md:text-left w-full md:w-4/5 px-2 md:px-4 py-2 md:py-3">
                <h2 className="text-accent-primary text-lg md:text-2xl font-semibold leading-snug">
                  {babyWonderWeek?.title || "No milestone yet"}
                </h2>
                <p className="text-accent-secondary mt-1 text-sm md:text-base leading-snug md:leading-normal">
                  {babyWonderWeek?.description ||
                    "Take care and enjoy these early days — milestones will appear soon."}
                </p>
              </div>
            </div>
          </div>
          {/* Upcoming Tasks Section */}
          <div className="px-3 pt-3">
            <div className="pt-3 flex flex-col">
              {/* Top row: Button + Title */}
              <div className="flex items-center">
                <button
                  onClick={() => setIsModalOpen(true)}
                  className="flex items-center justify-center w-12 h-12 rounded-full bg-accent-primary text-white text-3xl shadow hover:bg-accent-secondary transition mr-3"
                  title="Add New Reminder"
                >
                  +
                </button>

                <h1 className="text-accent-primary text-4xl md:text-5xl font-bold">
                  Upcoming Tasks
                </h1>
              </div>
              {/* Subtitle under the title, aligned with title start */}
              {categoryFilter !== "overdue" && categoryFilter !== "completed" && (
                <p className="text-gray-500 text-lg italic mt-1 ">
                  {dateLabel}
                </p>
              )}
            </div>
            {/* Category Filter */}
            {/* Converted from flex with fixed-width buttons to a responsive CSS grid so it fills available width */}
            <div className="grid grid-cols-2 sm:grid-cols-3 md:grid-cols-5 gap-3 mt-2 mb-8 w-full">
              {[
                {
                  key: "all",
                  label: "All",
                  icon: "/logo/other/reminder_all.png",
                },
                {
                  key: "tasks",
                  label: "Tasks",
                  icon: "/logo/other/reminder_task.png",
                },
                {
                  key: "vaccinations",
                  label: "Vaccinations",
                  icon: "/logo/other/reminder_vaccination.png",
                },
                {
                  key: "completed",
                  label: "Completed",
                  icon: "/logo/other/reminder_complete.png",
                },
                {
                  key: "overdue",
                  label: "Overdue",
                  icon: "/logo/other/reminder_overdue.png",
                },
              ].map((cat) => {
                const active = categoryFilter === cat.key;
                return (
                  <button
                    key={cat.key}
                    onClick={() => setCategoryFilter(cat.key as any)}
                    className={`flex flex-col items-center justify-center rounded-[20px] shadow-md border transition-all duration-200 ease-in-out p-3 min-h-[88px] min-w-0 break-words overflow-hidden
                      ${
                        active
                          ? "bg-accent-primary/90 text-white scale-105"
                          : "bg-light text-dark-grey hover:bg-accent-primary/20"
                      }`}
                  >
                    <div className="flex justify-center items-center w-full mb-2">
                      <img
                        src={cat.icon}
                        alt={cat.label}
                        className={`h-14 w-auto ${active ? "opacity-100" : "opacity-80"}`}
                      />
                    </div>
                    <span
                      className={`text-sm font-semibold tracking-wide text-center w-full px-1 ${
                        active ? "text-white" : "text-gray-700"
                      }`}
                    >
                      {cat.label}
                    </span>
                  </button>
                );
              })}
            </div>
            {/* Time Filter */}
            {categoryFilter !== "overdue" && categoryFilter !== "completed" && (
              <div className="grid grid-cols-2 sm:grid-cols-4 gap-3 mb-3 w-full">
                {" "}
                {[
                  {
                    key: "all",
                    label: "All Dates",
                  },
                  {
                    key: "daily",
                    label: "Today",
                  },
                  {
                    key: "weekly",
                    label: "This Week",
                  },
                  {
                    key: "monthly",
                    label: "This Month",
                  },
                ].map((opt) => (
                  <button
                    key={opt.key}
                    onClick={() => setTimeFilter(opt.key as any)}
                    className={`px-3 py-1.5 rounded-full text-sm font-medium transition
          ${
            timeFilter === opt.key
              ? "bg-accent-primary text-white shadow-md scale-105"
              : "bg-gray-200 text-gray-700 hover:bg-gray-300"
          }`}
                  >
                    {opt.label}
                  </button>
                ))}{" "}
              </div>
            )}
            {/* Task Lists rendered from backend type buckets */}
            {visibleCategoryEntries.map(([category, items]) => (
              <div key={category} className="mt-8">
                <h2 className="text-accent-secondary text-2xl font-semibold pb-2 capitalize">
                  {category}
                </h2>
                <ul className="mt-2 space-y-2 text-black w-full">
                  {items.length === 0 && (
                    <div
                      className="w-full shadow-md rounded-xl flex flex-col md:flex-row items-center overflow-hidden"
                      style={{
                        background:
                          "linear-gradient(to bottom left, var(--light-blue), white)",
                      }}
                    >
                      {/* Image section */}
                      <div className="flex justify-center items-center w-full md:w-1/5 p-3 flex-shrink-0">
                        <div className="relative w-24 h-24 md:w-28 md:h-28">
                          <Image
                            src={noItemImages[category] || noItemImages.default}
                            alt={`${category} placeholder image`}
                            fill
                            className="object-contain"
                            priority
                          />
                        </div>
                      </div>

                      {/* Text section */}
                      <div className="flex flex-col justify-center text-center md:text-left relative md:relative w-full md:w-4/5 z-0">
                        <h2 className="text-accent-primary text-xl md:text-2xl font-semibold leading-tight capitalize">
                          No {category}
                        </h2>
                        <p className="text-accent-secondary mt-1 text-base leading-snug md:leading-normal">
                          There's nothing to show right now.
                        </p>
                      </div>
                    </div>
                  )}

                  {items
                    .slice()
                    .sort((a, b) => a.title.localeCompare(b.title))
                    .map((r) => (
                      <li
                        key={r.id}
                        className="flex justify-between items-center rounded-lg border border-light-grey px-3 py-2 shadow-sm bg-primary/20 hover:bg-secondary/20 transition text-lg"
                      >
                        <div className="flex items-center gap-3">
                          {r.range !== ReminderRange.COMPLETED && (
                            <input
                              type="checkbox"
                              className="accent-accent-primary border-accent-primary mr-2"
                              checked={
                                String(r.range) === ReminderRange.COMPLETED
                              }
                              onChange={() => handleToggleComplete(r)}
                              title={"Mark complete"}
                            />
                          )}
                          <div className="flex flex-col">
                            <span className="font-medium"> {r.title}</span>
                            {r.description && (
                              <span className="text-sm text-gray-600">
                                {r.description}
                              </span>
                            )}
                            {r.range !== ReminderRange.COMPLETED && r.nextDue && (
                              <span className="text-xs text-gray-500">
                                Next due:{" "}
                                {new Date(r.nextDue).toLocaleDateString()}
                              </span>
                            )}
                          </div>
                        </div>
                        {(r.type === ReminderType.TASK || r.type === ReminderType.VACCINATION) &&
                          r.range !== ReminderRange.COMPLETED && (
                            <div className="flex items-center gap-2">
                              <button
                                onClick={() => openEditModal(r)}
                                className="text-accent-primary px-2 py-1 rounded-md hover:bg-gray-200 transition text-lg"
                                title="Edit"
                              >
                                …
                              </button>
                              <button
                                onClick={() => handleDelete(r)}
                                className="text-red-600 px-2 py-1 rounded-md hover:bg-red-50 transition text-lg"
                                title="Delete"
                              >
                                ✕
                              </button>
                            </div>
                          )}
                      </li>
                    ))}
                </ul>
              </div>
            ))}
          </div>
          {isModalOpen && (
            <ReminderModal
              taskName={newTaskName}
              setTaskName={setNewTaskName}
              taskDescription={newTaskDescription}
              setTaskDescription={setNewTaskDescription}
              repeat={newTaskRepeat}
              setRepeat={setNewTaskRepeat}
              date={newTaskStartDate}
              setDate={setNewTaskStartDate}
              minDate={tomorrowStr}
              errorMessage={addModelErrorMessage}
              onCancel={handleCancel}
              onSubmit={handleAddTask}
              modeType={null}
              setModeType={setAddModeType}
              submitLabel="Add"
            />
          )}
          {isEditModalOpen && editingReminder && (
            <ReminderModal
              taskName={editedTitle}
              setTaskName={setEditedTitle}
              taskDescription={editTaskDescription}
              setTaskDescription={setEditTaskDescription}
              repeat={editedRepeat}
              minDate={tomorrowStr}
              setRepeat={setEditedRepeat}
              date={nextDueDate}
              setDate={setNextDueDate}
              errorMessage={editModelErrorMessage}
              modeType={ReminderType[editingReminder.type].toLowerCase() as "task" | "vaccination"}
              onCancel={() => {
                setIsEditModalOpen(false);
                setEditingReminder(null);
                setEditModelErrorMessage("");
              }}
              onSubmit={handleSaveEdit}
              submitLabel="Save"
            />
          )}
        </div>
      </div>
    </div>
  );
}
