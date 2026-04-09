'use client';

import { useEffect, useState } from 'react';
import { useParams, useRouter } from 'next/navigation';
import { apiClient, Patient, Medecin } from '@/lib/api-client';
import { FormLayout, FormField, FormGrid } from '@/components/form-layout';
import { Input } from '@/components/ui/input';
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from '@/components/ui/select';
import { Skeleton } from '@/components/ui/skeleton';
import { Card, CardContent } from '@/components/ui/card';
import { AppLayout } from '@/components/app-layout';

export default function EditPatientPage() {
  const params = useParams();
  const router = useRouter();
  const patientId = parseInt(params.id as string);
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [medecins, setMedecins] = useState<Medecin[]>([]);
  const [formData, setFormData] = useState<Partial<Patient>>({
    nom: '',
    prenom: '',
    email: '',
    telephone: '',
    adresse: '',
    dateNaissance: '',
    numeroSecuriteSociale: '',
    mutuelle: '',
    personneContact: '',
    telephoneContact: '',
    medecinTraitantId: undefined,
  });

  useEffect(() => {
    const fetchData = async () => {
      try {
        const [patientData, medecinsData] = await Promise.all([
          apiClient.getPatientById(patientId),
          apiClient.getMedecins(),
        ]);
        setFormData(patientData);
        setMedecins(medecinsData);
      } catch (error) {
        console.error('Failed to fetch data:', error);
      } finally {
        setLoading(false);
      }
    };

    fetchData();
  }, [patientId]);

  const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const { name, value } = e.target;
    setFormData((prev) => ({ ...prev, [name]: value }));
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setSaving(true);
    try {
      await apiClient.updatePatient(patientId, formData);
      router.push(`/patients/${patientId}`);
    } catch (error) {
      console.error('Failed to update patient:', error);
    } finally {
      setSaving(false);
    }
  };

  if (loading) {
    return (
      <AppLayout title="Chargement..." description="Veuillez patienter">
        <div className="mx-auto max-w-3xl space-y-6">
          {[...Array(3)].map((_, i) => (
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
      title={`Modifier ${formData.prenom} ${formData.nom}`}
      description="Mettre a jour les informations du patient"
      backHref={`/patients/${patientId}`}
      onSubmit={handleSubmit}
      isSubmitting={saving}
      submitLabel="Enregistrer les modifications"
      sections={[
        {
          title: 'Informations personnelles',
          description: 'Les informations de base du patient',
          children: (
            <div className="space-y-4">
              <FormGrid>
                <FormField label="Nom" required>
                  <Input
                    name="nom"
                    value={formData.nom || ''}
                    onChange={handleChange}
                    required
                  />
                </FormField>
                <FormField label="Prenom" required>
                  <Input
                    name="prenom"
                    value={formData.prenom || ''}
                    onChange={handleChange}
                    required
                  />
                </FormField>
              </FormGrid>
              <FormGrid>
                <FormField label="Email" required>
                  <Input
                    name="email"
                    type="email"
                    value={formData.email || ''}
                    onChange={handleChange}
                    required
                  />
                </FormField>
                <FormField label="Telephone" required>
                  <Input
                    name="telephone"
                    value={formData.telephone || ''}
                    onChange={handleChange}
                    required
                  />
                </FormField>
              </FormGrid>
              <FormField label="Adresse">
                <Input
                  name="adresse"
                  value={formData.adresse || ''}
                  onChange={handleChange}
                />
              </FormField>
              <FormGrid>
                <FormField label="Date de naissance">
                  <Input
                    name="dateNaissance"
                    type="date"
                    value={formData.dateNaissance ? formData.dateNaissance.split('T')[0] : ''}
                    onChange={handleChange}
                  />
                </FormField>
                <FormField label="Numero Securite Sociale">
                  <Input
                    name="numeroSecuriteSociale"
                    value={formData.numeroSecuriteSociale || ''}
                    onChange={handleChange}
                  />
                </FormField>
              </FormGrid>
            </div>
          ),
        },
        {
          title: 'Informations medicales',
          description: 'Couverture sante et medecin traitant',
          children: (
            <FormGrid>
              <FormField label="Mutuelle">
                <Input
                  name="mutuelle"
                  value={formData.mutuelle || ''}
                  onChange={handleChange}
                />
              </FormField>
              <FormField label="Medecin traitant">
                <Select
                  value={formData.medecinTraitantId ? String(formData.medecinTraitantId) : ''}
                  onValueChange={(value) =>
                    setFormData((prev) => ({ ...prev, medecinTraitantId: value ? parseInt(value) : undefined }))
                  }
                >
                  <SelectTrigger>
                    <SelectValue placeholder="Selectionner un medecin" />
                  </SelectTrigger>
                  <SelectContent>
                    {medecins.map((m) => (
                      <SelectItem key={m.id} value={String(m.id)}>
                        Dr. {m.prenom} {m.nom}
                      </SelectItem>
                    ))}
                  </SelectContent>
                </Select>
              </FormField>
            </FormGrid>
          ),
        },
        {
          title: 'Contact d&apos;urgence',
          description: 'Personne a contacter en cas d&apos;urgence',
          children: (
            <FormGrid>
              <FormField label="Nom du contact">
                <Input
                  name="personneContact"
                  value={formData.personneContact || ''}
                  onChange={handleChange}
                />
              </FormField>
              <FormField label="Telephone du contact">
                <Input
                  name="telephoneContact"
                  value={formData.telephoneContact || ''}
                  onChange={handleChange}
                />
              </FormField>
            </FormGrid>
          ),
        },
      ]}
    />
  );
}
