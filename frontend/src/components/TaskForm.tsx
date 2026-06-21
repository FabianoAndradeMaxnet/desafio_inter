import { FormEvent, useState } from "react";
import type { CreateTaskPayload } from "../types/task";

interface TaskFormProps {
  isSubmitting: boolean;
  onSubmit: (payload: CreateTaskPayload) => Promise<void>;
}

const TITLE_MIN_LENGTH = 3;
const TITLE_MAX_LENGTH = 120;
const DESCRIPTION_MAX_LENGTH = 500;

export function TaskForm({ isSubmitting, onSubmit }: TaskFormProps) {
  const [title, setTitle] = useState("");
  const [description, setDescription] = useState("");
  const [validationError, setValidationError] = useState<string | null>(null);

  async function handleSubmit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();

    const normalizedTitle = title.trim();
    const normalizedDescription = description.trim();

    if (normalizedTitle.length < TITLE_MIN_LENGTH) {
      setValidationError(`Titulo deve ter pelo menos ${TITLE_MIN_LENGTH} caracteres.`);
      return;
    }

    if (normalizedTitle.length > TITLE_MAX_LENGTH) {
      setValidationError(`Titulo deve ter no maximo ${TITLE_MAX_LENGTH} caracteres.`);
      return;
    }

    if (normalizedDescription.length > DESCRIPTION_MAX_LENGTH) {
      setValidationError(`Descricao deve ter no maximo ${DESCRIPTION_MAX_LENGTH} caracteres.`);
      return;
    }

    setValidationError(null);

    await onSubmit({
      title: normalizedTitle,
      description: normalizedDescription.length > 0 ? normalizedDescription : null,
    });

    setTitle("");
    setDescription("");
  }

  return (
    <form className="task-form" onSubmit={handleSubmit}>
      <div className="field-group">
        <label htmlFor="task-title">Titulo</label>
        <input
          id="task-title"
          type="text"
          value={title}
          maxLength={TITLE_MAX_LENGTH}
          disabled={isSubmitting}
          onChange={(event) => setTitle(event.target.value)}
          placeholder="Ex: Revisar contrato"
        />
      </div>

      <div className="field-group">
        <label htmlFor="task-description">Descricao</label>
        <textarea
          id="task-description"
          value={description}
          maxLength={DESCRIPTION_MAX_LENGTH}
          disabled={isSubmitting}
          onChange={(event) => setDescription(event.target.value)}
          placeholder="Detalhes opcionais"
          rows={3}
        />
      </div>

      {validationError && <p className="inline-error">{validationError}</p>}

      <button type="submit" className="primary-button" disabled={isSubmitting}>
        {isSubmitting ? "Criando..." : "Criar tarefa"}
      </button>
    </form>
  );
}
