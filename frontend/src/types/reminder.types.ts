export interface BabyReminder {
  id: number;
  babyId: number;
  type: ReminderType;
  title: string;
  description?: string;
  frequency: Frequency;
  occurrence?: number;
  requiresAction?: boolean;
  notes?: string;
  completedOn?: string | null;
  nextDue?: string | null;
  range?: ReminderRange;
  userCreated?: boolean;
  startDate?: string | null;
}

export enum Frequency {
  DAILY = 'DAILY',
  WEEKLY = 'WEEKLY',
  MONTHLY = 'MONTHLY',
  QUARTERLY = 'QUARTERLY',
  ANNUAL = 'ANNUAL',
  ONCE = 'ONCE',
}

export enum ReminderRange {
  COMPLETED = 'COMPLETED',
  OVERDUE = 'OVERDUE',
  TODAY = 'TODAY',
  UPCOMING_WEEK = 'UPCOMING_WEEK',
  UPCOMING_MONTH = 'UPCOMING_MONTH',
  FUTURE = 'FUTURE',
}

export enum ReminderType {
  TASK = 'TASK',
  VACCINATION = 'VACCINATION',
  MILESTONE = 'MILESTONE',
}


