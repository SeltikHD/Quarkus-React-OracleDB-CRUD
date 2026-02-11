import type { ReactElement } from 'react';

import FactoryIcon from '@mui/icons-material/Factory';
import InventoryIcon from '@mui/icons-material/Inventory2';
import PrecisionManufacturingIcon from '@mui/icons-material/PrecisionManufacturing';
import ScienceIcon from '@mui/icons-material/Science';
import {
  Drawer,
  List,
  ListItemButton,
  ListItemIcon,
  ListItemText,
  Toolbar,
  Typography,
  Box,
  Divider,
} from '@mui/material';
import { useLocation, useNavigate } from 'react-router-dom';

const DRAWER_WIDTH = 260;

interface INavItem {
  label: string;
  path: string;
  icon: React.ReactNode;
}

const NAV_ITEMS: INavItem[] = [
  { label: 'Raw Materials', path: '/raw-materials', icon: <ScienceIcon /> },
  { label: 'Products', path: '/products', icon: <InventoryIcon /> },
  { label: 'Production Planning', path: '/production', icon: <PrecisionManufacturingIcon /> },
];

function Sidebar(): ReactElement {
  const location = useLocation();
  const navigate = useNavigate();

  return (
    <Drawer
      sx={{
        width: DRAWER_WIDTH,
        flexShrink: 0,
        '& .MuiDrawer-paper': {
          width: DRAWER_WIDTH,
          boxSizing: 'border-box',
          bgcolor: 'background.paper',
        },
      }}
      variant="permanent"
    >
      <Toolbar sx={{ gap: 1 }}>
        <FactoryIcon color="primary" />
        <Typography color="primary" fontWeight={700} noWrap variant="h6">
          Autoflex ERP
        </Typography>
      </Toolbar>
      <Divider />
      <Box sx={{ overflow: 'auto', mt: 1 }}>
        <List disablePadding>
          {NAV_ITEMS.map((item) => {
            const selected = location.pathname.startsWith(item.path);
            return (
              <ListItemButton
                key={item.path}
                selected={selected}
                sx={{
                  mx: 1,
                  borderRadius: 1,
                  mb: 0.5,
                  '&.Mui-selected': {
                    bgcolor: 'primary.main',
                    color: 'primary.contrastText',
                    '& .MuiListItemIcon-root': { color: 'primary.contrastText' },
                    '&:hover': { bgcolor: 'primary.dark' },
                  },
                }}
                onClick={async () => {
                  await navigate(item.path);
                }}
              >
                <ListItemIcon sx={{ minWidth: 40 }}>{item.icon}</ListItemIcon>
                <ListItemText primary={item.label} />
              </ListItemButton>
            );
          })}
        </List>
      </Box>
    </Drawer>
  );
}

export { DRAWER_WIDTH };
export default Sidebar;
