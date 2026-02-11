import type { ReactElement } from 'react';

import NotificationsNoneIcon from '@mui/icons-material/NotificationsNone';
import { AppBar, Toolbar, Typography, IconButton, Box } from '@mui/material';

import { DRAWER_WIDTH } from './Sidebar';

interface INavbarProps {
  title: string;
}

function Navbar({ title }: INavbarProps): ReactElement {
  return (
    <AppBar
      position="fixed"
      sx={{
        width: `calc(100% - ${String(DRAWER_WIDTH)}px)`,
        ml: `${String(DRAWER_WIDTH)}px`,
        bgcolor: 'background.paper',
        color: 'text.primary',
      }}
    >
      <Toolbar>
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
