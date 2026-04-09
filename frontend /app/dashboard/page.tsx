'use client';

import { useEffect, useState } from 'react';
import Link from 'next/link';
import { useAuth } from '@/lib/auth-context';
import { apiClient, RendezVous, Patient } from '@/lib/api-client';
import { AppLayout } from '@/components/app-layout';
import { StatCard } from '@/components/stat-card';
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { Button } from '@/components/ui/button';
import { Badge } from '@/components/ui/badge';
import { Avatar, AvatarFallback } from '@/components/ui/avatar';
import { Skeleton } from '@/components/ui/skeleton';
import {
  Users,
  UserCog,
  Calendar,
  Activity,
  Clock,
  ArrowRight,
  Plus,
  TrendingUp,
} from 'lucide-react';

interface DashboardStats {
  totalPatients: number;
  totalMedecins: number;
  totalSpecialites: number;
  totalRendezVous: number;
  rdvAujourdhui: number;
}

export default function DashboardPage() {
  const { user } = useAuth();
  const [stats, setStats] = useState<DashboardStats>({
    totalPatients: 0,
    totalMedecins: 0,
    totalSpecialites: 0,
    totalRendezVous: 0,
    rdvAujourdhui: 0,
  });
  const [rdvDuJour, setRdvDuJour] = useState<RendezVous[]>([]);
  const [derniersPatients, setDerniersPatients] = useState<Patient[]>([]);
  const [statsLoading, setStatsLoading] = useState(true);

  useEffect(() => {
    const fetchStats = async () => {
      try {
        const [patients, medecins, specialites, rendezvous, rdvJour, patients10] = await Promise.all([
          apiClient.getPatients().then(p => ({ length: p.length })),
          apiClient.getMedecins().then(m => ({ length: m.length })),
          apiClient.getSpecialites().then(s => ({ length: s.length })),
          apiClient.getRendezVous().then(r => ({ length: r.length })),
          apiClient.getRendezVousDuJour(),
          apiClient.getLastPatients(5),
        ]);

        setStats({
          totalPatients: patients.length,
          totalMedecins: medecins.length,
          totalSpecialites: specialites.length,
          totalRendezVous: rendezvous.length,
          rdvAujourdhui: rdvJour.length,
        });
        setRdvDuJour(rdvJour);
        setDerniersPatients(patients10);
      } catch (error) {
        console.error('Failed to fetch stats:', error);
      } finally {
        setStatsLoading(false);
      }
    };

    if (user) {
      fetchStats();
    }
  }, [user]);

  const getStatusColor = (status: string) => {
    switch (status.toLowerCase()) {
      case 'confirme':
        return 'bg-success/10 text-success border-success/20';
      case 'en_attente':
        return 'bg-warning/10 text-warning-foreground border-warning/20';
      case 'annule':
        return 'bg-destructive/10 text-destructive border-destructive/20';
      default:
        return 'bg-muted text-muted-foreground';
    }
  };

  return (
    <AppLayout
      title={`Bonjour, ${user?.prenom || ''}`}
      description="Voici un apercu de votre clinique aujourd&apos;hui"
    >
      {/* Stats Grid */}
      <div className="grid gap-4 md:grid-cols-2 lg:grid-cols-4 xl:grid-cols-5">
        {statsLoading ? (
          <>
            {[...Array(5)].map((_, i) => (
              <Card key={i} className="p-5">
                <Skeleton className="h-4 w-20 mb-3" />
                <Skeleton className="h-8 w-16" />
              </Card>
            ))}
          </>
        ) : (
          <>
            <StatCard
              label="Total Patients"
              value={stats.totalPatients}
              icon={Users}
            />
            <StatCard
              label="Medecins"
              value={stats.totalMedecins}
              icon={UserCog}
            />
            <StatCard
              label="Specialites"
              value={stats.totalSpecialites}
              icon={Activity}
            />
            <StatCard
              label="Rendez-vous"
              value={stats.totalRendezVous}
              icon={Calendar}
            />
            <StatCard
              label="RDV Aujourd&apos;hui"
              value={stats.rdvAujourdhui}
              icon={TrendingUp}
              variant="primary"
            />
          </>
        )}
      </div>

      {/* Main Content Grid */}
      <div className="mt-6 grid gap-6 lg:grid-cols-3">
        {/* Quick Actions */}
        <Card className="lg:col-span-1">
          <CardHeader className="pb-3">
            <CardTitle className="text-base font-semibold">Actions rapides</CardTitle>
          </CardHeader>
          <CardContent className="space-y-2">
            {user?.role === 'ADMIN' && (
              <>
                <Link href="/patients/new" className="block">
                  <Button className="w-full justify-start gap-2" variant="secondary">
                    <Plus className="h-4 w-4" />
                    Nouveau patient
                  </Button>
                </Link>
                <Link href="/appointments/new" className="block">
                  <Button className="w-full justify-start gap-2" variant="secondary">
                    <Calendar className="h-4 w-4" />
                    Nouveau rendez-vous
                  </Button>
                </Link>
                <Link href="/doctors" className="block">
                  <Button className="w-full justify-start gap-2" variant="secondary">
                    <UserCog className="h-4 w-4" />
                    Gerer les medecins
                  </Button>
                </Link>
                <Link href="/specialties" className="block">
                  <Button className="w-full justify-start gap-2" variant="secondary">
                    <Activity className="h-4 w-4" />
                    Gerer les specialites
                  </Button>
                </Link>
              </>
            )}
            {user?.role === 'MEDECIN' && (
              <>
                <Link href="/consultations" className="block">
                  <Button className="w-full justify-start gap-2" variant="secondary">
                    <Activity className="h-4 w-4" />
                    Mes consultations
                  </Button>
                </Link>
              </>
            )}
          </CardContent>
        </Card>

        {/* Today's Appointments */}
        <Card className="lg:col-span-2">
          <CardHeader className="flex flex-row items-center justify-between pb-3">
            <CardTitle className="text-base font-semibold">
              Rendez-vous du jour
            </CardTitle>
            <Link href="/appointments">
              <Button variant="ghost" size="sm" className="gap-1 text-muted-foreground hover:text-foreground">
                Voir tout
                <ArrowRight className="h-4 w-4" />
              </Button>
            </Link>
          </CardHeader>
          <CardContent>
            {statsLoading ? (
              <div className="space-y-3">
                {[...Array(3)].map((_, i) => (
                  <div key={i} className="flex items-center gap-4 rounded-lg border p-3">
                    <Skeleton className="h-10 w-10 rounded-full" />
                    <div className="flex-1 space-y-2">
                      <Skeleton className="h-4 w-32" />
                      <Skeleton className="h-3 w-24" />
                    </div>
                    <Skeleton className="h-6 w-16" />
                  </div>
                ))}
              </div>
            ) : rdvDuJour.length === 0 ? (
              <div className="flex flex-col items-center justify-center py-8 text-center">
                <Calendar className="h-12 w-12 text-muted-foreground/50" />
                <p className="mt-3 text-sm text-muted-foreground">
                  Aucun rendez-vous prevu aujourd&apos;hui
                </p>
              </div>
            ) : (
              <div className="space-y-3">
                {rdvDuJour.slice(0, 5).map((rdv) => (
                  <div
                    key={rdv.id}
                    className="flex items-center gap-4 rounded-lg border border-border bg-card p-3 transition-colors hover:bg-muted/50"
                  >
                    <Avatar className="h-10 w-10">
                      <AvatarFallback className="bg-primary/10 text-primary text-sm font-medium">
                        {rdv.patient.prenom[0]}{rdv.patient.nom[0]}
                      </AvatarFallback>
                    </Avatar>
                    <div className="flex-1 min-w-0">
                      <p className="font-medium text-foreground truncate">
                        {rdv.patient.prenom} {rdv.patient.nom}
                      </p>
                      <p className="text-sm text-muted-foreground truncate">
                        {rdv.motif}
                      </p>
                    </div>
                    <div className="text-right shrink-0">
                      <div className="flex items-center gap-1.5 text-sm font-medium text-foreground">
                        <Clock className="h-3.5 w-3.5 text-muted-foreground" />
                        {new Date(rdv.dateHeure).toLocaleTimeString('fr-FR', {
                          hour: '2-digit',
                          minute: '2-digit',
                        })}
                      </div>
                      <Badge variant="outline" className={getStatusColor(rdv.statut)}>
                        {rdv.statut}
                      </Badge>
                    </div>
                  </div>
                ))}
              </div>
            )}
          </CardContent>
        </Card>
      </div>

      {/* Recent Patients */}
      <Card className="mt-6">
        <CardHeader className="flex flex-row items-center justify-between pb-3">
          <CardTitle className="text-base font-semibold">
            Derniers patients inscrits
          </CardTitle>
          <Link href="/patients">
            <Button variant="ghost" size="sm" className="gap-1 text-muted-foreground hover:text-foreground">
              Voir tout
              <ArrowRight className="h-4 w-4" />
            </Button>
          </Link>
        </CardHeader>
        <CardContent>
          {statsLoading ? (
            <div className="space-y-3">
              {[...Array(3)].map((_, i) => (
                <div key={i} className="flex items-center gap-4">
                  <Skeleton className="h-10 w-10 rounded-full" />
                  <div className="flex-1 space-y-2">
                    <Skeleton className="h-4 w-32" />
                    <Skeleton className="h-3 w-48" />
                  </div>
                </div>
              ))}
            </div>
          ) : derniersPatients.length === 0 ? (
            <div className="flex flex-col items-center justify-center py-8 text-center">
              <Users className="h-12 w-12 text-muted-foreground/50" />
              <p className="mt-3 text-sm text-muted-foreground">
                Aucun patient enregistre
              </p>
            </div>
          ) : (
            <div className="overflow-x-auto">
              <table className="w-full">
                <thead>
                  <tr className="border-b">
                    <th className="pb-3 text-left text-xs font-semibold uppercase tracking-wider text-muted-foreground">
                      Patient
                    </th>
                    <th className="pb-3 text-left text-xs font-semibold uppercase tracking-wider text-muted-foreground">
                      Email
                    </th>
                    <th className="pb-3 text-left text-xs font-semibold uppercase tracking-wider text-muted-foreground">
                      Telephone
                    </th>
                    <th className="pb-3 text-left text-xs font-semibold uppercase tracking-wider text-muted-foreground">
                      Statut
                    </th>
                    <th className="pb-3 text-right text-xs font-semibold uppercase tracking-wider text-muted-foreground">
                      Action
                    </th>
                  </tr>
                </thead>
                <tbody className="divide-y divide-border">
                  {derniersPatients.map((patient) => (
                    <tr key={patient.id} className="group">
                      <td className="py-3">
                        <div className="flex items-center gap-3">
                          <Avatar className="h-8 w-8">
                            <AvatarFallback className="bg-secondary text-secondary-foreground text-xs">
                              {patient.prenom[0]}{patient.nom[0]}
                            </AvatarFallback>
                          </Avatar>
                          <span className="font-medium text-foreground">
                            {patient.prenom} {patient.nom}
                          </span>
                        </div>
                      </td>
                      <td className="py-3 text-sm text-muted-foreground">
                        {patient.email}
                      </td>
                      <td className="py-3 text-sm text-muted-foreground">
                        {patient.telephone}
                      </td>
                      <td className="py-3">
                        <Badge variant="outline" className="bg-success/10 text-success border-success/20">
                          {patient.statut}
                        </Badge>
                      </td>
                      <td className="py-3 text-right">
                        <Link href={`/patients/${patient.id}`}>
                          <Button size="sm" variant="ghost" className="opacity-0 group-hover:opacity-100 transition-opacity">
                            Voir
                          </Button>
                        </Link>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          )}
        </CardContent>
      </Card>
    </AppLayout>
  );
}
