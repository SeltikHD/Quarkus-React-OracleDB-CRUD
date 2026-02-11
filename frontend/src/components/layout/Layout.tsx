import { useCallback, useState } from 'react';

import type { ReactElement } from 'react';

import { Box, Toolbar, useMediaQuery, useTheme } from '@mui/material';
import { Outlet, useLocation } from 'react-router-dom';

import Navbar from './Navbar';
import Sidebar, { DRAWER_WIDTH } from './Sidebar';

/**
 * Derives a human-readable page title from the current route path.
 */
function getPageTitle(pathname: string): string {
  if (pathname.startsWith('/products')) {
    return 'Products';
  }
  if (pathname.startsWith('/raw-materials')) {
    return 'Raw Materials';
  }
  if (pathname.startsWith('/production')) {
    return 'Production Planning';
  }
  return 'Dashboard';
}

/**
 * Main application layout with responsive sidebar and top navbar.
 *
 * Desktop (md+): Sidebar is permanent/docked.
 * Mobile (<md): Sidebar is temporary, toggled via hamburger menu.
 */
function Layout(): ReactElement {
  const location = useLocation();
  const title = getPageTitle(location.pathname);
  const theme = useTheme();
  const isMobile = useMediaQuery(theme.breakpoints.down('md'));
  const [mobileOpen, setMobileOpen] = useState(false);

  const handleDrawerToggle = useCallback((): void => {
    setMobileOpen((prev) => !prev);
  }, []);

  return (
    <Box sx={{ display: 'flex', minHeight: '100vh' }}>
      <Sidebar mobileOpen={mobileOpen} onToggle={handleDrawerToggle} />
      <Navbar title={title} onMenuToggle={handleDrawerToggle} />
      <Box
        component="main"
        sx={{
          flexGrow: 1,
          p: 3,
          width: isMobile ? '100%' : `calc(100% - ${String(DRAWER_WIDTH)}px)`,
          bgcolor: 'background.default',
        }}
      >
        {/* Spacer for the fixed AppBar */}
        <Toolbar />
        <Outlet />
      </Box>
    </Box>
  );
}

export default Layout;
