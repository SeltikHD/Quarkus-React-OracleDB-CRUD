import type { ReactElement } from 'react';

import MenuIcon from '@mui/icons-material/Menu';
import NotificationsNoneIcon from '@mui/icons-material/NotificationsNone';
import {
  AppBar,
  Toolbar,
  Typography,
  IconButton,
  Box,
  useMediaQuery,
  useTheme,
} from '@mui/material';

import { DRAWER_WIDTH } from './Sidebar';

interface INavbarProps {
  title: string;
  onMenuToggle: () => void;
}

function Navbar({ title, onMenuToggle }: Readonly<INavbarProps>): ReactElement {
  const theme = useTheme();
  const isMobile = useMediaQuery(theme.breakpoints.down('md'));

  return (
    <AppBar
      position="fixed"
      sx={{
        width: isMobile ? '100%' : `calc(100% - ${String(DRAWER_WIDTH)}px)`,
        ml: isMobile ? 0 : `${String(DRAWER_WIDTH)}px`,
        bgcolor: 'background.paper',
        color: 'text.primary',
      }}
    >
      <Toolbar>
        {isMobile && (
          <IconButton
            aria-label="open navigation menu"
            color="inherit"
            data-testid="menu-toggle-btn"
            edge="start"
            sx={{ mr: 2 }}
            onClick={onMenuToggle}
          >
            <MenuIcon />
          </IconButton>
        )}
        <Typography component="h1" noWrap sx={{ flexGrow: 1 }} variant="h6">
          {title}
        </Typography>
        <Box>
          <IconButton size="large">
            <NotificationsNoneIcon />
          </IconButton>
        </Box>
      </Toolbar>
    </AppBar>
  );
}

export default Navbar;
