'use client';

import { useEffect, useState } from 'react';
import { useParams } from 'next/navigation';
import Link from 'next/link';
import { apiClient, Patient, Consultation } from '@/lib/api-client';
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
  MapPin,
  Calendar,
  Shield,
  Stethoscope,
  ClipboardList,
  User,
  FileText,
} from 'lucide-react';

export default function PatientDetailPage() {
  const params = useParams();
  const patientId = parseInt(params.id as string);
  const [patient, setPatient] = useState<Patient | null>(null);
  const [consultations, setConsultations] = useState<Consultation[]>([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const fetchData = async () => {
      try {
        const [patientData, consultationsData] = await Promise.all([
          apiClient.getPatientById(patientId),
          apiClient.getConsultationsByPatient(patientId),
        ]);
        setPatient(patientData);
        setConsultations(consultationsData);
      } catch (error) {
        console.error('Failed to fetch patient:', error);
      } finally {
        setLoading(false);
      }
    };

    fetchData();
  }, [patientId]);

  const getStatusColor = (status: string) => {
    switch (status?.toLowerCase()) {
      case 'terminee':
        return 'bg-success/10 text-success border-success/20';
      case 'en_cours':
        return 'bg-primary/10 text-primary border-primary/20';
      case 'annulee':
        return 'bg-destructive/10 text-destructive border-destructive/20';
      default:
        return 'bg-secondary text-secondary-foreground';
    }
  };

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

  if (!patient) {
    return (
      <AppLayout title="Patient non trouve" description="Ce patient n&apos;existe pas">
        <Card className="p-12 text-center">
          <User className="mx-auto h-12 w-12 text-muted-foreground/50" />
          <p className="mt-4 text-muted-foreground">Ce patient n&apos;existe pas ou a ete supprime.</p>
          <Link href="/patients" className="mt-4 inline-block">
            <Button variant="outline">Retour a la liste</Button>
          </Link>
        </Card>
      </AppLayout>
    );
  }

  return (
    <AppLayout
      title={`${patient.prenom} ${patient.nom}`}
      description="Fiche patient"
      actions={
        <Link href={`/patients/${patient.id}/edit`}>
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
                    {patient.prenom[0]}{patient.nom[0]}
                  </AvatarFallback>
                </Avatar>
                <h2 className="mt-4 text-xl font-semibold">{patient.prenom} {patient.nom}</h2>
                <Badge
                  variant="outline"
                  className={patient.statut === 'ACTIF'
                    ? 'mt-2 bg-success/10 text-success border-success/20'
                    : 'mt-2 bg-muted text-muted-foreground'
                  }
                >
                  {patient.statut}
                </Badge>
              </div>

              <div className="mt-6 space-y-3 border-t pt-6">
                <div className="flex items-center gap-3 text-sm">
                  <Mail className="h-4 w-4 text-muted-foreground" />
                  <span className="text-muted-foreground truncate">{patient.email}</span>
                </div>
                <div className="flex items-center gap-3 text-sm">
                  <Phone className="h-4 w-4 text-muted-foreground" />
                  <span className="text-muted-foreground">{patient.telephone}</span>
                </div>
                {patient.adresse && (
                  <div className="flex items-center gap-3 text-sm">
                    <MapPin className="h-4 w-4 text-muted-foreground" />
                    <span className="text-muted-foreground">{patient.adresse}</span>
                  </div>
                )}
                <div className="flex items-center gap-3 text-sm">
                  <Calendar className="h-4 w-4 text-muted-foreground" />
                  <span className="text-muted-foreground">
                    {new Date(patient.dateNaissance).toLocaleDateString('fr-FR')}
                  </span>
                </div>
              </div>
            </CardContent>
          </Card>

          {/* Medical Info */}
          <Card>
            <CardHeader className="pb-3">
              <CardTitle className="text-base flex items-center gap-2">
                <Shield className="h-4 w-4 text-primary" />
                Couverture sante
              </CardTitle>
            </CardHeader>
            <CardContent className="space-y-3 text-sm">
              <div>
                <p className="text-muted-foreground">Numero SS</p>
                <p className="font-medium">{patient.numeroSecuriteSociale || 'Non renseigne'}</p>
              </div>
              <div>
                <p className="text-muted-foreground">Mutuelle</p>
                <p className="font-medium">{patient.mutuelle || 'Non renseigne'}</p>
              </div>
            </CardContent>
          </Card>

          {/* Treating Doctor */}
          <Card>
            <CardHeader className="pb-3">
              <CardTitle className="text-base flex items-center gap-2">
                <Stethoscope className="h-4 w-4 text-primary" />
                Medecin traitant
              </CardTitle>
            </CardHeader>
            <CardContent>
              {patient.medecinTraitant ? (
                <div className="flex items-center gap-3">
                  <Avatar className="h-10 w-10">
                    <AvatarFallback className="bg-secondary text-secondary-foreground text-sm">
                      {patient.medecinTraitant.prenom[0]}{patient.medecinTraitant.nom[0]}
                    </AvatarFallback>
                  </Avatar>
                  <div>
                    <p className="font-medium">
                      Dr. {patient.medecinTraitant.prenom} {patient.medecinTraitant.nom}
                    </p>
                    <Link
                      href={`/doctors/${patient.medecinTraitant.id}`}
                      className="text-xs text-primary hover:underline"
                    >
                      Voir le profil
                    </Link>
                  </div>
                </div>
              ) : (
                <p className="text-sm text-muted-foreground">Aucun medecin assigne</p>
              )}
            </CardContent>
          </Card>
        </div>

        {/* Consultations */}
        <div className="lg:col-span-2">
          <Card>
            <CardHeader className="flex flex-row items-center justify-between">
              <CardTitle className="flex items-center gap-2">
                <ClipboardList className="h-5 w-5 text-primary" />
                Historique des consultations
              </CardTitle>
              <Badge variant="secondary">{consultations.length}</Badge>
            </CardHeader>
            <CardContent>
              {consultations.length === 0 ? (
                <div className="py-8 text-center">
                  <FileText className="mx-auto h-10 w-10 text-muted-foreground/50" />
                  <p className="mt-3 text-sm text-muted-foreground">Aucune consultation</p>
                </div>
              ) : (
                <div className="space-y-3">
                  {consultations.map((consultation) => (
                    <div
                      key={consultation.id}
                      className="flex items-start justify-between gap-4 rounded-lg border p-4 transition-colors hover:bg-muted/50"
                    >
                      <div className="flex-1 min-w-0">
                        <div className="flex items-center gap-2">
                          <p className="font-medium">{consultation.motif}</p>
                          <Badge variant="outline" className={getStatusColor(consultation.statut)}>
                            {consultation.statut}
                          </Badge>
                        </div>
                        <p className="mt-1 text-sm text-muted-foreground line-clamp-1">
                          {consultation.diagnostic || 'Pas de diagnostic'}
                        </p>
                        <div className="mt-2 flex items-center gap-4 text-xs text-muted-foreground">
                          <span className="flex items-center gap-1">
                            <Calendar className="h-3 w-3" />
                            {new Date(consultation.dateConsultation).toLocaleDateString('fr-FR')}
                          </span>
                          <span className="flex items-center gap-1">
                            <Stethoscope className="h-3 w-3" />
                            Dr. {consultation.medecinPrenom} {consultation.medecinNom}
                          </span>
                        </div>
                      </div>
                      <Link href={`/consultations/${consultation.id}/edit`}>
                        <Button variant="ghost" size="sm">
                          Voir
                        </Button>
                      </Link>
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
