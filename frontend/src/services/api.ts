import type {
  ApiResponse,
  CreateTaskPayload,
  PageResponse,
  Task,
  TaskStatus,
} from "../types/task";

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL ?? "";
const TASKS_PATH = `${API_BASE_URL}/api/v1/tasks`;

export class ApiRequestError extends Error {
  readonly code: string;
  readonly details: string[];
  readonly status: number;

  constructor(message: string, status: number, code = "REQUEST_FAILED", details: string[] = []) {
    super(message);
    this.name = "ApiRequestError";
    this.status = status;
    this.code = code;
    this.details = details;
  }
}

export async function listTasks(page = 0, size = 20): Promise<PageResponse<Task>> {
  return requestPage<Task>(`${TASKS_PATH}?page=${page}&size=${size}`);
}

export async function createTask(payload: CreateTaskPayload): Promise<Task> {
  return requestData<Task>(TASKS_PATH, {
    method: "POST",
    body: JSON.stringify(payload),
  });
}

export async function updateTaskStatus(taskId: string, status: TaskStatus): Promise<Task> {
  return requestData<Task>(`${TASKS_PATH}/${taskId}/status`, {
    method: "PATCH",
    body: JSON.stringify({ status }),
  });
}

async function requestData<T>(url: string, init?: RequestInit): Promise<T> {
  const response = await request<ApiResponse<T>>(url, init);

  if (response.error) {
    throw new ApiRequestError(response.error.message, 400, response.error.code, response.error.details);
  }

  if (response.data === null) {
    throw new ApiRequestError("Resposta da API sem dados.", 500, "EMPTY_RESPONSE");
  }

  return response.data;
}

async function requestPage<T>(url: string, init?: RequestInit): Promise<PageResponse<T>> {
  const response = await request<PageResponse<T>>(url, init);

  if (response.error) {
    throw new ApiRequestError(response.error.message, 400, response.error.code, response.error.details);
  }

  return {
    ...response,
    data: Array.isArray(response.data) ? response.data : [],
  };
}

async function request<T>(url: string, init?: RequestInit): Promise<T> {
  let response: Response;

  try {
    response = await fetch(url, {
      ...init,
      headers: {
        "Content-Type": "application/json",
        Accept: "application/json",
        ...init?.headers,
      },
    });
  } catch (error) {
    throw new ApiRequestError(
      "Nao foi possivel conectar ao backend.",
      0,
      "NETWORK_ERROR",
      [error instanceof Error ? error.message : "Erro de rede desconhecido."],
    );
  }

  const body = await parseJson<T | ApiResponse<unknown> | PageResponse<unknown>>(response);

  if (!response.ok) {
    const apiError = extractApiError(body);
    throw new ApiRequestError(
      apiError?.message ?? "Requisicao rejeitada pela API.",
      response.status,
      apiError?.code,
      apiError?.details,
    );
  }

  return body as T;
}

async function parseJson<T>(response: Response): Promise<T> {
  const text = await response.text();

  if (!text) {
    return {} as T;
  }

  try {
    return JSON.parse(text) as T;
  } catch {
    throw new ApiRequestError("Resposta invalida recebida da API.", response.status, "INVALID_JSON_RESPONSE");
  }
}

function extractApiError(body: unknown) {
  if (!body || typeof body !== "object" || !("error" in body)) {
    return null;
  }

  const error = (body as { error?: unknown }).error;

  if (!error || typeof error !== "object") {
    return null;
  }

  const candidate = error as { code?: unknown; message?: unknown; details?: unknown };
  return {
    code: typeof candidate.code === "string" ? candidate.code : "API_ERROR",
    message: typeof candidate.message === "string" ? candidate.message : "Erro retornado pela API.",
    details: Array.isArray(candidate.details) ? candidate.details.filter(isString) : [],
  };
}

function isString(value: unknown): value is string {
  return typeof value === "string";
}
