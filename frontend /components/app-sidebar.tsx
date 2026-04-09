'use client';

import Link from 'next/link';
import { usePathname } from 'next/navigation';
import { useAuth } from '@/lib/auth-context';
import { cn } from '@/lib/utils';
import {
  LayoutDashboard,
  Users,
  UserCog,
  Calendar,
  Stethoscope,
  ClipboardList,
  LogOut,
  Activity,
  ChevronLeft,
  Menu,
} from 'lucide-react';
import { Button } from '@/components/ui/button';
import { Avatar, AvatarFallback } from '@/components/ui/avatar';
import { useState } from 'react';

interface NavItem {
  label: string;
  href: string;
  icon: React.ElementType;
  roles?: string[];
}

const navItems: NavItem[] = [
  { label: 'Tableau de bord', href: '/dashboard', icon: LayoutDashboard },
  { label: 'Patients', href: '/patients', icon: Users, roles: ['ADMIN'] },
  { label: 'Medecins', href: '/doctors', icon: UserCog, roles: ['ADMIN'] },
  { label: 'Rendez-vous', href: '/appointments', icon: Calendar, roles: ['ADMIN'] },
  { label: 'Specialites', href: '/specialties', icon: Activity, roles: ['ADMIN'] },
  { label: 'Consultations', href: '/consultations', icon: Stethoscope },
];

export function AppSidebar() {
  const pathname = usePathname();
  const { user } = useAuth();
  const [collapsed, setCollapsed] = useState(false);

  const filteredNavItems = navItems.filter(
    (item) => !item.roles || (user?.role && item.roles.includes(user.role))
  );

  return (
    <>
      {/* Mobile overlay */}
      <div
        className={cn(
          'fixed inset-0 z-40 bg-foreground/20 backdrop-blur-sm transition-opacity lg:hidden',
          collapsed ? 'opacity-0 pointer-events-none' : 'opacity-100'
        )}
        onClick={() => setCollapsed(true)}
      />

      {/* Sidebar */}
      <aside
        className={cn(
          'fixed left-0 top-0 z-50 flex h-screen flex-col bg-sidebar text-sidebar-foreground transition-all duration-300 ease-in-out',
          collapsed ? 'w-[70px]' : 'w-[260px]',
          'lg:relative'
        )}
      >
        {/* Logo Section */}
        <div className="flex h-16 items-center justify-between border-b border-sidebar-border px-4">
          <Link href="/dashboard" className="flex items-center gap-3">
            <div className="flex h-9 w-9 items-center justify-center rounded-lg bg-sidebar-primary">
              <Stethoscope className="h-5 w-5 text-sidebar-primary-foreground" />
            </div>
            {!collapsed && (
              <span className="text-lg font-semibold tracking-tight">
                MediCare
              </span>
            )}
          </Link>
          <Button
            variant="ghost"
            size="icon"
            onClick={() => setCollapsed(!collapsed)}
            className="h-8 w-8 text-sidebar-foreground hover:bg-sidebar-accent hover:text-sidebar-accent-foreground"
          >
            {collapsed ? (
              <Menu className="h-4 w-4" />
            ) : (
              <ChevronLeft className="h-4 w-4" />
            )}
          </Button>
        </div>

        {/* Navigation */}
        <nav className="flex-1 space-y-1 overflow-y-auto p-3">
          <div className={cn('mb-2 px-3 text-xs font-medium uppercase tracking-wider text-sidebar-foreground/50', collapsed && 'hidden')}>
            Menu principal
          </div>
          {filteredNavItems.map((item) => {
            const isActive = pathname === item.href || pathname.startsWith(`${item.href}/`);
            return (
              <Link
                key={item.href}
                href={item.href}
                className={cn(
                  'group flex items-center gap-3 rounded-lg px-3 py-2.5 text-sm font-medium transition-all',
                  isActive
                    ? 'bg-sidebar-primary text-sidebar-primary-foreground'
                    : 'text-sidebar-foreground/80 hover:bg-sidebar-accent hover:text-sidebar-accent-foreground',
                  collapsed && 'justify-center px-2'
                )}
              >
                <item.icon className={cn('h-5 w-5 shrink-0', isActive ? 'text-sidebar-primary-foreground' : 'text-sidebar-foreground/60 group-hover:text-sidebar-accent-foreground')} />
                {!collapsed && <span>{item.label}</span>}
              </Link>
            );
          })}
        </nav>

        {/* User Section */}
        <div className="border-t border-sidebar-border p-3">
          <div
            className={cn(
              'flex items-center gap-3 rounded-lg bg-sidebar-accent/50 p-3',
              collapsed && 'justify-center p-2'
            )}
          >
            <Avatar className="h-9 w-9 shrink-0">
              <AvatarFallback className="bg-sidebar-primary text-sidebar-primary-foreground text-sm font-medium">
                {user?.prenom?.[0]}
                {user?.nom?.[0]}
              </AvatarFallback>
            </Avatar>
            {!collapsed && (
              <div className="flex-1 overflow-hidden">
                <p className="truncate text-sm font-medium">
                  {user?.prenom} {user?.nom}
                </p>
                <p className="truncate text-xs text-sidebar-foreground/60">
                  {user?.role === 'ADMIN' ? 'Administrateur' : 'Medecin'}
                </p>
              </div>
            )}
          </div>
          <Link
            href="/logout"
            className={cn(
              'mt-2 flex items-center gap-3 rounded-lg px-3 py-2.5 text-sm font-medium text-sidebar-foreground/80 transition-all hover:bg-destructive/10 hover:text-destructive',
              collapsed && 'justify-center px-2'
            )}
          >
            <LogOut className="h-5 w-5 shrink-0" />
            {!collapsed && <span>Deconnexion</span>}
          </Link>
        </div>
      </aside>
    </>
  );
}
