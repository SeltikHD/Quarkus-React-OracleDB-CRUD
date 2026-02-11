import { useCallback, useState } from 'react';

import type { ReactElement } from 'react';

import { Alert, Snackbar } from '@mui/material';

import type { AlertColor } from '@mui/material';

interface INotification {
  open: boolean;
  message: string;
  severity: AlertColor;
}

interface IUseNotificationReturn {
  notification: INotification;
  showSuccess: (message: string) => void;
  showError: (message: string) => void;
  closeNotification: () => void;
}

/**
 * Custom hook that encapsulates snackbar notification state and actions.
 */
// eslint-disable-next-line react-refresh/only-export-components
export function useNotification(): IUseNotificationReturn {
  const [notification, setNotification] = useState<INotification>({
    open: false,
    message: '',
    severity: 'success',
  });

  const showSuccess = useCallback((message: string): void => {
    setNotification({ open: true, message, severity: 'success' });
  }, []);

  const showError = useCallback((message: string): void => {
    setNotification({ open: true, message, severity: 'error' });
  }, []);

  const closeNotification = useCallback((): void => {
    setNotification((prev) => ({ ...prev, open: false }));
  }, []);

  return { notification, showSuccess, showError, closeNotification };
}

interface INotificationSnackbarProps {
  notification: INotification;
  onClose: () => void;
}

/**
 * Reusable snackbar notification component.
 */
function NotificationSnackbar({
  notification,
  onClose,
}: Readonly<INotificationSnackbarProps>): ReactElement {
  return (
    <Snackbar autoHideDuration={4000} open={notification.open} onClose={onClose}>
      <Alert
        severity={notification.severity}
        sx={{ width: '100%' }}
        variant="filled"
        onClose={onClose}
      >
        {notification.message}
      </Alert>
    </Snackbar>
  );
}

export default NotificationSnackbar;
