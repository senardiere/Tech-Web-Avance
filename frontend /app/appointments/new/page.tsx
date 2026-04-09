'use client';

import { useState, useEffect } from 'react';
import { useRouter } from 'next/navigation';
import { apiClient, Patient, Medecin, RendezVous } from '@/lib/api-client';
import { FormLayout, FormField, FormGrid } from '@/components/form-layout';
import { Input } from '@/components/ui/input';
import { Skeleton } from '@/components/ui/skeleton';
import { Card, CardContent } from '@/components/ui/card';
import { AppLayout } from '@/components/app-layout';
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from '@/components/ui/select';

export default function NewAppointmentPage() {
  const router = useRouter();
  const [loading, setLoading] = useState(false);
  const [dataLoading, setDataLoading] = useState(true);
  const [patients, setPatients] = useState<Patient[]>([]);
  const [medecins, setMedecins] = useState<Medecin[]>([]);

  const [formData, setFormData] = useState<Partial<RendezVous>>({
    patientId: undefined,
    medecinId: undefined,
    dateHeure: '',
    duree: 30,
    motif: '',
    statut: 'PREVU',
  });

  useEffect(() => {
    const fetchData = async () => {
      try {
        const [patientsData, medecinsData] = await Promise.all([
          apiClient.getPatients(),
          apiClient.getMedecins(),
        ]);
        setPatients(patientsData);
        setMedecins(medecinsData);
      } catch (error) {
        console.error('Failed to fetch data:', error);
      } finally {
        setDataLoading(false);
      }
    };

    fetchData();
  }, []);

  const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const { name, value } = e.target;
    setFormData((prev) => ({
      ...prev,
      [name]: name === 'duree' ? parseInt(value) : value,
    }));
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setLoading(true);
    try {
      await apiClient.createRendezVous(formData);
      router.push('/appointments');
    } catch (error) {
      console.error('Failed to create appointment:', error);
    } finally {
      setLoading(false);
    }
  };

  if (dataLoading) {
    return (
      <AppLayout title="Chargement..." description="Veuillez patienter">
        <div className="mx-auto max-w-3xl space-y-6">
          {[...Array(2)].map((_, i) => (
            <Card key={i}>
              <CardContent className="pt-6 space-y-4">
                <Skeleton className="h-6 w-48" />
                <div className="grid grid-cols-2 gap-4">
                  <Skeleton className="h-10 w-full" />
                  <Skeleton className="h-10 w-full" />
                </div>
              </CardContent>
            </Card>
          ))}
        </div>
      </AppLayout>
    );
  }

  return (
    <FormLayout
      title="Nouveau rendez-vous"
      description="Planifier un nouveau rendez-vous medical"
      backHref="/appointments"
      onSubmit={handleSubmit}
      isSubmitting={loading}
      submitLabel="Creer le rendez-vous"
      sections={[
        {
          title: 'Participants',
          description: 'Selectionner le patient et le medecin',
          children: (
            <FormGrid>
              <FormField label="Patient" required>
                <Select
                  value={formData.patientId ? String(formData.patientId) : ''}
                  onValueChange={(value) =>
                    setFormData((prev) => ({ ...prev, patientId: parseInt(value) }))
                  }
                >
                  <SelectTrigger>
                    <SelectValue placeholder="Selectionner un patient" />
                  </SelectTrigger>
                  <SelectContent>
                    {patients.map((p) => (
                      <SelectItem key={p.id} value={String(p.id)}>
                        {p.prenom} {p.nom}
                      </SelectItem>
                    ))}
                  </SelectContent>
                </Select>
              </FormField>
              <FormField label="Medecin" required>
                <Select
                  value={formData.medecinId ? String(formData.medecinId) : ''}
                  onValueChange={(value) =>
                    setFormData((prev) => ({ ...prev, medecinId: parseInt(value) }))
                  }
                >
                  <SelectTrigger>
                    <SelectValue placeholder="Selectionner un medecin" />
                  </SelectTrigger>
                  <SelectContent>
                    {medecins.map((m) => (
                      <SelectItem key={m.id} value={String(m.id)}>
                        Dr. {m.prenom} {m.nom} - {m.specialite?.nom}
                      </SelectItem>
                    ))}
                  </SelectContent>
                </Select>
              </FormField>
            </FormGrid>
          ),
        },
        {
          title: 'Details du rendez-vous',
          description: 'Date, heure et motif de la consultation',
          children: (
            <div className="space-y-4">
              <FormGrid>
                <FormField label="Date et heure" required>
                  <Input
                    name="dateHeure"
                    type="datetime-local"
                    value={formData.dateHeure || ''}
                    onChange={handleChange}
                    required
                  />
                </FormField>
                <FormField label="Duree (minutes)" required>
                  <Select
                    value={String(formData.duree || 30)}
                    onValueChange={(value) =>
                      setFormData((prev) => ({ ...prev, duree: parseInt(value) }))
                    }
                  >
                    <SelectTrigger>
                      <SelectValue />
                    </SelectTrigger>
                    <SelectContent>
                      <SelectItem value="15">15 minutes</SelectItem>
                      <SelectItem value="30">30 minutes</SelectItem>
                      <SelectItem value="45">45 minutes</SelectItem>
                      <SelectItem value="60">1 heure</SelectItem>
                      <SelectItem value="90">1h30</SelectItem>
                    </SelectContent>
                  </Select>
                </FormField>
              </FormGrid>
              <FormField label="Motif" required>
                <Input
                  name="motif"
                  value={formData.motif || ''}
                  onChange={handleChange}
                  required
                  placeholder="Ex: Consultation de routine, Suivi post-operatoire..."
                />
              </FormField>
            </div>
          ),
        },
      ]}
    />
  );
}
