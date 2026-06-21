interface ErrorMessageProps {
  message: string;
  details?: string[];
  onRetry?: () => void;
}

export function ErrorMessage({ message, details = [], onRetry }: ErrorMessageProps) {
  return (
    <section className="error-box" role="alert">
      <div>
        <strong>{message}</strong>
        {details.length > 0 && (
          <ul>
            {details.map((detail) => (
              <li key={detail}>{detail}</li>
            ))}
          </ul>
        )}
      </div>
      {onRetry && (
        <button type="button" className="secondary-button" onClick={onRetry}>
          Tentar novamente
        </button>
      )}
    </section>
  );
}
