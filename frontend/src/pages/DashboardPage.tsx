import type { ReactElement } from 'react';

import FactoryIcon from '@mui/icons-material/Factory';
import InventoryIcon from '@mui/icons-material/Inventory2';
import PrecisionManufacturingIcon from '@mui/icons-material/PrecisionManufacturing';
import ScienceIcon from '@mui/icons-material/Science';
import { Box, Card, CardActionArea, CardContent, Typography } from '@mui/material';
import { useNavigate } from 'react-router-dom';

interface IDashboardCard {
  title: string;
  description: string;
  path: string;
  icon: React.ReactNode;
  color: string;
}

const CARDS: IDashboardCard[] = [
  {
    title: 'Raw Materials',
    description: 'Manage raw material inventory, create new materials, and adjust stock levels.',
    path: '/raw-materials',
    icon: <ScienceIcon sx={{ fontSize: 48 }} />,
    color: '#ed6c02',
  },
  {
    title: 'Products',
    description: 'Create products, manage bill of materials, and track inventory.',
    path: '/products',
    icon: <InventoryIcon sx={{ fontSize: 48 }} />,
    color: '#1976d2',
  },
  {
    title: 'Production Planning',
    description: 'Calculate optimal production plans based on available raw material stock.',
    path: '/production',
    icon: <PrecisionManufacturingIcon sx={{ fontSize: 48 }} />,
    color: '#2e7d32',
  },
];

function DashboardPage(): ReactElement {
  const navigate = useNavigate();

  return (
    <Box>
      <Box sx={{ mb: 4 }}>
        <Box sx={{ display: 'flex', alignItems: 'center', gap: 2, mb: 1 }}>
          <FactoryIcon color="primary" sx={{ fontSize: 40 }} />
          <Typography component="h2" fontWeight={700} variant="h4">
            Autoflex ERP
          </Typography>
        </Box>
        <Typography color="text.secondary" variant="body1">
          Product and Raw Material Management System
        </Typography>
      </Box>

      <Box
        sx={{
          display: 'grid',
          gridTemplateColumns: 'repeat(auto-fit, minmax(300px, 1fr))',
          gap: 3,
        }}
      >
        {CARDS.map((card) => (
          <Card key={card.path} sx={{ height: '100%' }}>
            <CardActionArea
              sx={{ height: '100%', p: 1 }}
              onClick={() => {
                void navigate(card.path);
              }}
            >
              <CardContent sx={{ textAlign: 'center' }}>
                <Box sx={{ color: card.color, mb: 2 }}>{card.icon}</Box>
                <Typography gutterBottom fontWeight={600} variant="h6">
                  {card.title}
                </Typography>
                <Typography color="text.secondary" variant="body2">
                  {card.description}
                </Typography>
              </CardContent>
            </CardActionArea>
          </Card>
        ))}
      </Box>
    </Box>
  );
}

export default DashboardPage;
