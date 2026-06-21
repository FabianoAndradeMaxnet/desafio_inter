interface LoadingProps {
  label?: string;
}

export function Loading({ label = "Carregando tarefas..." }: LoadingProps) {
  return (
    <div className="loading-state" aria-live="polite">
      <span className="spinner" aria-hidden="true" />
      <span>{label}</span>
    </div>
  );
}
