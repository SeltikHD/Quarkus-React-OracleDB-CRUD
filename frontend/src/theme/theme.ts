import { createTheme, responsiveFontSizes } from '@mui/material/styles';

/**
 * Autoflex MUI Theme Configuration.
 *
 * This theme provides a professional, modern look for the ERP application.
 * Colors are chosen for readability and accessibility (WCAG 2.1 AA compliance).
 */

// Brand colors
const primaryMain = '#1976d2'; // Professional blue
const secondaryMain = '#dc004e'; // Accent red
const successMain = '#2e7d32'; // Success green
const warningMain = '#ed6c02'; // Warning orange
const errorMain = '#d32f2f'; // Error red

// Create base theme
const theme = createTheme({
  palette: {
    mode: 'light',
    primary: {
      main: primaryMain,
      light: '#42a5f5',
      dark: '#1565c0',
      contrastText: '#ffffff',
    },
    secondary: {
      main: secondaryMain,
      light: '#ff4081',
      dark: '#9a0036',
      contrastText: '#ffffff',
    },
    success: {
      main: successMain,
      light: '#4caf50',
      dark: '#1b5e20',
    },
    warning: {
      main: warningMain,
      light: '#ff9800',
      dark: '#e65100',
    },
    error: {
      main: errorMain,
      light: '#ef5350',
      dark: '#c62828',
    },
    background: {
      default: '#f5f5f5',
      paper: '#ffffff',
    },
    text: {
      primary: 'rgba(0, 0, 0, 0.87)',
      secondary: 'rgba(0, 0, 0, 0.6)',
    },
  },

  typography: {
    fontFamily: [
      'Roboto',
      '-apple-system',
      'BlinkMacSystemFont',
      '"Segoe UI"',
      '"Helvetica Neue"',
      'Arial',
      'sans-serif',
    ].join(','),
    h1: {
      fontWeight: 500,
      fontSize: '2.5rem',
    },
    h2: {
      fontWeight: 500,
      fontSize: '2rem',
    },
    h3: {
      fontWeight: 500,
      fontSize: '1.75rem',
    },
    h4: {
      fontWeight: 500,
      fontSize: '1.5rem',
    },
    h5: {
      fontWeight: 500,
      fontSize: '1.25rem',
    },
    h6: {
      fontWeight: 500,
      fontSize: '1rem',
    },
    button: {
      textTransform: 'none', // Disable uppercase transformation
      fontWeight: 500,
    },
  },

  shape: {
    borderRadius: 8,
  },

  components: {
    // Global component overrides
    MuiButton: {
      styleOverrides: {
        root: {
          borderRadius: 8,
          padding: '8px 16px',
        },
        contained: {
          boxShadow: 'none',
          '&:hover': {
            boxShadow: '0 2px 4px rgba(0,0,0,0.15)',
          },
        },
      },
      defaultProps: {
        disableElevation: true,
      },
    },

    MuiCard: {
      styleOverrides: {
        root: {
          borderRadius: 12,
          boxShadow: '0 2px 8px rgba(0,0,0,0.08)',
        },
      },
    },

    MuiTextField: {
      defaultProps: {
        variant: 'outlined',
        size: 'small',
      },
    },

    MuiTableHead: {
      styleOverrides: {
        root: {
          backgroundColor: '#f5f5f5',
          '& .MuiTableCell-head': {
            fontWeight: 600,
          },
        },
      },
    },

    MuiTableRow: {
      styleOverrides: {
        root: {
          '&:hover': {
            backgroundColor: 'rgba(0, 0, 0, 0.02)',
          },
        },
      },
    },

    MuiAppBar: {
      defaultProps: {
        elevation: 0,
      },
      styleOverrides: {
        root: {
          borderBottom: '1px solid rgba(0, 0, 0, 0.12)',
        },
      },
    },

    MuiDrawer: {
      styleOverrides: {
        paper: {
          borderRight: '1px solid rgba(0, 0, 0, 0.12)',
        },
      },
    },

    MuiChip: {
      styleOverrides: {
        root: {
          fontWeight: 500,
        },
      },
    },

    MuiAlert: {
      styleOverrides: {
        root: {
          borderRadius: 8,
        },
      },
    },
  },
});

// Apply responsive font sizes
const responsiveTheme = responsiveFontSizes(theme);

export default responsiveTheme;
