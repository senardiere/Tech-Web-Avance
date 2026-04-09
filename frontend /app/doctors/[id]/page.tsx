'use client';

import { useEffect, useState } from 'react';
import { useParams } from 'next/navigation';
import Link from 'next/link';
import { apiClient, Medecin, Patient, RendezVous } from '@/lib/api-client';
import { AppLayout } from '@/components/app-layout';
import { Button } from '@/components/ui/button';
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { Badge } from '@/components/ui/badge';
import { Avatar, AvatarFallback } from '@/components/ui/avatar';
import { Skeleton } from '@/components/ui/skeleton';
import {
  Pencil,
  Mail,
  Phone,
  Building,
  Award,
  Stethoscope,
  Users,
  CalendarCheck,
  CalendarClock,
  Clock,
  User,
} from 'lucide-react';

export default function DoctorDetailPage() {
  const params = useParams();
  const doctorId = parseInt(params.id as string);
  const [doctor, setDoctor] = useState<Medecin | null>(null);
  const [patients, setPatients] = useState<Patient[]>([]);
  const [appointments, setAppointments] = useState<RendezVous[]>([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const fetchData = async () => {
      try {
        const [doctorData, patientsData, appointmentsData] = await Promise.all([
          apiClient.getMedecinById(doctorId),
          apiClient.getPatients().then((p) => p.filter((pat) => pat.medecinTraitant?.id === doctorId)),
          apiClient.getRendezVousByMedecin(doctorId),
        ]);
        setDoctor(doctorData);
        setPatients(patientsData);
        setAppointments(appointmentsData);
      } catch (error) {
        console.error('Failed to fetch doctor:', error);
      } finally {
        setLoading(false);
      }
    };

    fetchData();
  }, [doctorId]);

  const upcomingAppointments = appointments.filter((a) => new Date(a.dateHeure) > new Date());

  if (loading) {
    return (
      <AppLayout title="Chargement..." description="Veuillez patienter">
        <div className="grid gap-6 lg:grid-cols-3">
          <div className="lg:col-span-1">
            <Card className="p-6">
              <div className="flex flex-col items-center">
                <Skeleton className="h-24 w-24 rounded-full" />
                <Skeleton className="mt-4 h-6 w-40" />
                <Skeleton className="mt-2 h-4 w-24" />
              </div>
            </Card>
          </div>
          <div className="lg:col-span-2 space-y-6">
            <Card className="p-6">
              <Skeleton className="h-6 w-48 mb-4" />
              <div className="space-y-3">
                {[...Array(4)].map((_, i) => (
                  <Skeleton key={i} className="h-10 w-full" />
                ))}
              </div>
            </Card>
          </div>
        </div>
      </AppLayout>
    );
  }

  if (!doctor) {
    return (
      <AppLayout title="Medecin non trouve" description="Ce medecin n&apos;existe pas">
        <Card className="p-12 text-center">
          <User className="mx-auto h-12 w-12 text-muted-foreground/50" />
          <p className="mt-4 text-muted-foreground">Ce medecin n&apos;existe pas ou a ete supprime.</p>
          <Link href="/doctors" className="mt-4 inline-block">
            <Button variant="outline">Retour a la liste</Button>
          </Link>
        </Card>
      </AppLayout>
    );
  }

  return (
    <AppLayout
      title={`Dr. ${doctor.prenom} ${doctor.nom}`}
      description="Profil du medecin"
      actions={
        <Link href={`/doctors/${doctor.id}/edit`}>
          <Button className="gap-2">
            <Pencil className="h-4 w-4" />
            Modifier
          </Button>
        </Link>
      }
    >
      <div className="grid gap-6 lg:grid-cols-3">
        {/* Profile Card */}
        <div className="lg:col-span-1 space-y-6">
          <Card>
            <CardContent className="pt-6">
              <div className="flex flex-col items-center text-center">
                <Avatar className="h-24 w-24">
                  <AvatarFallback className="bg-primary/10 text-primary text-2xl font-semibold">
                    {doctor.prenom[0]}{doctor.nom[0]}
                  </AvatarFallback>
                </Avatar>
                <h2 className="mt-4 text-xl font-semibold">
                  Dr. {doctor.prenom} {doctor.nom}
                </h2>
                <Badge variant="outline" className="mt-2 bg-secondary">
                  {doctor.specialite?.nom || 'Non definie'}
                </Badge>
                <Badge
                  variant="outline"
                  className={doctor.actif
                    ? 'mt-2 bg-success/10 text-success border-success/20'
                    : 'mt-2 bg-destructive/10 text-destructive border-destructive/20'
                  }
                >
                  {doctor.actif ? 'Actif' : 'Inactif'}
                </Badge>
              </div>

              <div className="mt-6 space-y-3 border-t pt-6">
                <div className="flex items-center gap-3 text-sm">
                  <Mail className="h-4 w-4 text-muted-foreground" />
                  <span className="text-muted-foreground truncate">{doctor.email}</span>
                </div>
                {doctor.telephone && (
                  <div className="flex items-center gap-3 text-sm">
                    <Phone className="h-4 w-4 text-muted-foreground" />
                    <span className="text-muted-foreground">{doctor.telephone}</span>
                  </div>
                )}
                <div className="flex items-center gap-3 text-sm">
                  <Building className="h-4 w-4 text-muted-foreground" />
                  <span className="text-muted-foreground">{doctor.cabinet}</span>
                </div>
                <div className="flex items-center gap-3 text-sm">
                  <Award className="h-4 w-4 text-muted-foreground" />
                  <span className="text-muted-foreground">{doctor.numeroLicence}</span>
                </div>
              </div>
            </CardContent>
          </Card>

          {/* Statistics */}
          <Card>
            <CardHeader className="pb-3">
              <CardTitle className="text-base">Statistiques</CardTitle>
            </CardHeader>
            <CardContent className="space-y-4">
              <div className="flex items-center justify-between">
                <div className="flex items-center gap-2 text-sm text-muted-foreground">
                  <Users className="h-4 w-4" />
                  Patients
                </div>
                <span className="text-xl font-bold text-primary">{patients.length}</span>
              </div>
              <div className="flex items-center justify-between">
                <div className="flex items-center gap-2 text-sm text-muted-foreground">
                  <CalendarCheck className="h-4 w-4" />
                  Total RDV
                </div>
                <span className="text-xl font-bold text-primary">{appointments.length}</span>
              </div>
              <div className="flex items-center justify-between">
                <div className="flex items-center gap-2 text-sm text-muted-foreground">
                  <CalendarClock className="h-4 w-4" />
                  RDV a venir
                </div>
                <span className="text-xl font-bold text-success">{upcomingAppointments.length}</span>
              </div>
            </CardContent>
          </Card>
        </div>

        {/* Right Column */}
        <div className="lg:col-span-2 space-y-6">
          {/* Patients */}
          <Card>
            <CardHeader className="flex flex-row items-center justify-between">
              <CardTitle className="flex items-center gap-2">
                <Users className="h-5 w-5 text-primary" />
                Patients assignes
              </CardTitle>
              <Badge variant="secondary">{patients.length}</Badge>
            </CardHeader>
            <CardContent>
              {patients.length === 0 ? (
                <div className="py-8 text-center">
                  <User className="mx-auto h-10 w-10 text-muted-foreground/50" />
                  <p className="mt-3 text-sm text-muted-foreground">Aucun patient assigne</p>
                </div>
              ) : (
                <div className="grid gap-3 sm:grid-cols-2">
                  {patients.slice(0, 6).map((patient) => (
                    <Link
                      key={patient.id}
                      href={`/patients/${patient.id}`}
                      className="flex items-center gap-3 rounded-lg border p-3 transition-colors hover:bg-muted/50"
                    >
                      <Avatar className="h-10 w-10">
                        <AvatarFallback className="bg-primary/10 text-primary text-sm">
                          {patient.prenom[0]}{patient.nom[0]}
                        </AvatarFallback>
                      </Avatar>
                      <div className="min-w-0">
                        <p className="font-medium truncate">{patient.prenom} {patient.nom}</p>
                        <p className="text-xs text-muted-foreground truncate">{patient.email}</p>
                      </div>
                    </Link>
                  ))}
                </div>
              )}
            </CardContent>
          </Card>

          {/* Upcoming Appointments */}
          <Card>
            <CardHeader className="flex flex-row items-center justify-between">
              <CardTitle className="flex items-center gap-2">
                <CalendarClock className="h-5 w-5 text-primary" />
                Rendez-vous a venir
              </CardTitle>
              <Badge variant="secondary">{upcomingAppointments.length}</Badge>
            </CardHeader>
            <CardContent>
              {upcomingAppointments.length === 0 ? (
                <div className="py-8 text-center">
                  <CalendarClock className="mx-auto h-10 w-10 text-muted-foreground/50" />
                  <p className="mt-3 text-sm text-muted-foreground">Aucun rendez-vous a venir</p>
                </div>
              ) : (
                <div className="space-y-3">
                  {upcomingAppointments
                    .sort((a, b) => new Date(a.dateHeure).getTime() - new Date(b.dateHeure).getTime())
                    .slice(0, 5)
                    .map((appointment) => (
                      <div
                        key={appointment.id}
                        className="flex items-center justify-between gap-4 rounded-lg border p-4 transition-colors hover:bg-muted/50"
                      >
                        <div className="flex items-center gap-3">
                          <Avatar className="h-10 w-10">
                            <AvatarFallback className="bg-primary/10 text-primary text-sm">
                              {appointment.patient.prenom[0]}{appointment.patient.nom[0]}
                            </AvatarFallback>
                          </Avatar>
                          <div>
                            <p className="font-medium">
                              {appointment.patient.prenom} {appointment.patient.nom}
                            </p>
                            <p className="text-sm text-muted-foreground">{appointment.motif}</p>
                          </div>
                        </div>
                        <div className="text-right shrink-0">
                          <p className="font-medium text-primary">
                            {new Date(appointment.dateHeure).toLocaleDateString('fr-FR')}
                          </p>
                          <p className="flex items-center justify-end gap-1 text-sm text-muted-foreground">
                            <Clock className="h-3 w-3" />
                            {new Date(appointment.dateHeure).toLocaleTimeString('fr-FR', {
                              hour: '2-digit',
                              minute: '2-digit',
                            })}
                          </p>
                        </div>
                      </div>
                    ))}
                </div>
              )}
            </CardContent>
          </Card>
        </div>
      </div>
    </AppLayout>
  );
}
