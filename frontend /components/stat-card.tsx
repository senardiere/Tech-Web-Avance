import { cn } from '@/lib/utils';
import { Card } from '@/components/ui/card';
import { LucideIcon } from 'lucide-react';

interface StatCardProps {
  label: string;
  value: number | string;
  icon: LucideIcon;
  trend?: {
    value: number;
    isPositive: boolean;
  };
  variant?: 'default' | 'primary' | 'success' | 'warning';
}

export function StatCard({ label, value, icon: Icon, trend, variant = 'default' }: StatCardProps) {
  const variants = {
    default: {
      card: 'bg-card',
      icon: 'bg-secondary text-foreground',
      label: 'text-muted-foreground',
      value: 'text-foreground',
    },
    primary: {
      card: 'bg-primary text-primary-foreground',
      icon: 'bg-primary-foreground/20 text-primary-foreground',
      label: 'text-primary-foreground/80',
      value: 'text-primary-foreground',
    },
    success: {
      card: 'bg-success text-success-foreground',
      icon: 'bg-success-foreground/20 text-success-foreground',
      label: 'text-success-foreground/80',
      value: 'text-success-foreground',
    },
    warning: {
      card: 'bg-warning text-warning-foreground',
      icon: 'bg-warning-foreground/20 text-warning-foreground',
      label: 'text-warning-foreground/80',
      value: 'text-warning-foreground',
    },
  };

  const styles = variants[variant];

  return (
    <Card className={cn('relative overflow-hidden p-5', styles.card)}>
      <div className="flex items-start justify-between">
        <div className="space-y-2">
          <p className={cn('text-sm font-medium', styles.label)}>{label}</p>
          <p className={cn('text-3xl font-bold tracking-tight', styles.value)}>
            {typeof value === 'number' ? value.toLocaleString('fr-FR') : value}
          </p>
          {trend && (
            <p
              className={cn(
                'flex items-center text-xs font-medium',
                trend.isPositive ? 'text-success' : 'text-destructive',
                variant !== 'default' && 'opacity-80'
              )}
            >
              <span className="mr-1">{trend.isPositive ? '+' : ''}{trend.value}%</span>
              <span className="opacity-60">vs mois dernier</span>
            </p>
          )}
        </div>
        <div className={cn('rounded-xl p-3', styles.icon)}>
          <Icon className="h-5 w-5" />
        </div>
      </div>
    </Card>
  );
}
