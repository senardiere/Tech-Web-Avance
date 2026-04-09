'use client';

import { useEffect, useState } from 'react';
import { useParams, useRouter } from 'next/navigation';
import { apiClient, Medecin, Specialite } from '@/lib/api-client';
import { FormLayout, FormField, FormGrid } from '@/components/form-layout';
import { Input } from '@/components/ui/input';
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from '@/components/ui/select';
import { Switch } from '@/components/ui/switch';
import { Skeleton } from '@/components/ui/skeleton';
import { Card, CardContent } from '@/components/ui/card';
import { AppLayout } from '@/components/app-layout';

export default function EditDoctorPage() {
  const params = useParams();
  const router = useRouter();
  const doctorId = parseInt(params.id as string);
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [specialities, setSpecialities] = useState<Specialite[]>([]);
  const [formData, setFormData] = useState<Partial<Medecin>>({
    nom: '',
    prenom: '',
    email: '',
    telephone: '',
    cabinet: '',
    numeroLicence: '',
    actif: true,
  });

  useEffect(() => {
    const fetchData = async () => {
      try {
        const [doctorData, specialitiesData] = await Promise.all([
          apiClient.getMedecinById(doctorId),
          apiClient.getSpecialites(),
        ]);
        setFormData(doctorData);
        setSpecialities(specialitiesData);
      } catch (error) {
        console.error('Failed to fetch data:', error);
      } finally {
        setLoading(false);
      }
    };

    fetchData();
  }, [doctorId]);

  const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const { name, value } = e.target;
    setFormData((prev) => ({ ...prev, [name]: value }));
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setSaving(true);
    try {
      await apiClient.updateMedecin(doctorId, formData);
      router.push(`/doctors/${doctorId}`);
    } catch (error) {
      console.error('Failed to update doctor:', error);
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
      title={`Modifier Dr. ${formData.prenom} ${formData.nom}`}
      description="Mettre a jour les informations du medecin"
      backHref={`/doctors/${doctorId}`}
      onSubmit={handleSubmit}
      isSubmitting={saving}
      submitLabel="Enregistrer les modifications"
      sections={[
        {
          title: 'Informations personnelles',
          description: 'Les informations de base du medecin',
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
                <FormField label="Telephone">
                  <Input
                    name="telephone"
                    value={formData.telephone || ''}
                    onChange={handleChange}
                  />
                </FormField>
              </FormGrid>
            </div>
          ),
        },
        {
          title: 'Informations professionnelles',
          description: 'Licence, cabinet et specialite',
          children: (
            <div className="space-y-4">
              <FormGrid>
                <FormField label="Numero de licence">
                  <Input
                    name="numeroLicence"
                    value={formData.numeroLicence || ''}
                    onChange={handleChange}
                  />
                </FormField>
                <FormField label="Cabinet">
                  <Input
                    name="cabinet"
                    value={formData.cabinet || ''}
                    onChange={handleChange}
                  />
                </FormField>
              </FormGrid>
              <FormField label="Specialite">
                <Select
                  value={formData.specialite?.id ? String(formData.specialite.id) : ''}
                  onValueChange={(value) =>
                    setFormData((prev) => ({
                      ...prev,
                      specialite: specialities.find((s) => s.id === parseInt(value)),
                    }))
                  }
                >
                  <SelectTrigger>
                    <SelectValue placeholder="Selectionner une specialite" />
                  </SelectTrigger>
                  <SelectContent>
                    {specialities.map((s) => (
                      <SelectItem key={s.id} value={String(s.id)}>
                        {s.nom}
                      </SelectItem>
                    ))}
                  </SelectContent>
                </Select>
              </FormField>
            </div>
          ),
        },
        {
          title: 'Statut',
          description: 'Activer ou desactiver le compte du medecin',
          children: (
            <div className="flex items-center justify-between rounded-lg border p-4">
              <div>
                <p className="font-medium">Compte actif</p>
                <p className="text-sm text-muted-foreground">
                  Un medecin inactif ne peut plus se connecter au systeme
                </p>
              </div>
              <Switch
                checked={formData.actif ?? true}
                onCheckedChange={(checked) => setFormData((prev) => ({ ...prev, actif: checked }))}
              />
            </div>
          ),
        },
      ]}
    />
  );
}
