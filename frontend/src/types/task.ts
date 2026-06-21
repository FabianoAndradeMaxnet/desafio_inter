export const TASK_STATUSES = ["TODO", "IN_PROGRESS", "DONE", "CANCELLED"] as const;

export type TaskStatus = (typeof TASK_STATUSES)[number];

export interface Task {
  id: string;
  title: string;
  description: string | null;
  status: TaskStatus;
  createdAt: string;
  updatedAt: string;
}

export interface CreateTaskPayload {
  title: string;
  description?: string | null;
}

export interface UpdateTaskStatusPayload {
  status: TaskStatus;
}

export interface ApiError {
  code: string;
  message: string;
  details: string[];
}

export interface ApiResponse<T> {
  data: T | null;
  error: ApiError | null;
}

export interface PageMetadata {
  totalItems: number;
  page: number;
  size: number;
  totalPages: number;
}

export interface PageResponse<T> {
  data: T[];
  meta: PageMetadata;
  error: ApiError | null;
}

export type TaskRealtimeEventType = "CONNECTED" | "TASK_CREATED" | "TASK_STATUS_UPDATED";

export interface TaskRealtimeEvent {
  eventId: string;
  type: TaskRealtimeEventType;
  taskId: string | null;
  title: string | null;
  status: TaskStatus | null;
  previousStatus: TaskStatus | null;
  newStatus: TaskStatus | null;
  occurredAt: string;
}
