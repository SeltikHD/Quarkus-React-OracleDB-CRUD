import type { ReactElement } from 'react';

import { Box, Toolbar } from '@mui/material';
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
 * Main application layout with persistent sidebar and top navbar.
 */
function Layout(): ReactElement {
  const location = useLocation();
  const title = getPageTitle(location.pathname);

  return (
    <Box sx={{ display: 'flex', minHeight: '100vh' }}>
      <Sidebar />
      <Navbar title={title} />
      <Box
        component="main"
        sx={{
          flexGrow: 1,
          p: 3,
          width: `calc(100% - ${String(DRAWER_WIDTH)}px)`,
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
