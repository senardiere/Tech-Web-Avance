'use client';

import { useState, useEffect } from 'react';
import { useRouter } from 'next/navigation';
import { apiClient, Specialite } from '@/lib/api-client';
import { FormLayout, FormField, FormGrid } from '@/components/form-layout';
import { Input } from '@/components/ui/input';
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from '@/components/ui/select';

export default function NewDoctorPage() {
  const router = useRouter();
  const [loading, setLoading] = useState(false);
  const [specialities, setSpecialities] = useState<Specialite[]>([]);
  const [formData, setFormData] = useState({
    nom: '',
    prenom: '',
    email: '',
    login: '',
    motDePasse: '',
    telephone: '',
    numeroLicence: '',
    cabinet: '',
    specialiteId: '',
  });

  useEffect(() => {
    const fetchSpecialities = async () => {
      try {
        const data = await apiClient.getSpecialites();
        setSpecialities(data);
      } catch (error) {
        console.error('Failed to fetch specialities:', error);
      }
    };
    fetchSpecialities();
  }, []);

  const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const { name, value } = e.target;
    setFormData((prev) => ({ ...prev, [name]: value }));
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setLoading(true);
    try {
      const data = {
        ...formData,
        specialiteId: parseInt(formData.specialiteId),
      };
      await apiClient.createMedecin(data);
      router.push('/doctors');
    } catch (error) {
      console.error('Failed to create doctor:', error);
    } finally {
      setLoading(false);
    }
  };

  return (
    <FormLayout
      title="Nouveau medecin"
      description="Enregistrer un nouveau medecin dans le systeme"
      backHref="/doctors"
      onSubmit={handleSubmit}
      isSubmitting={loading}
      submitLabel="Creer le medecin"
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
                    value={formData.nom}
                    onChange={handleChange}
                    placeholder="Martin"
                    required
                  />
                </FormField>
                <FormField label="Prenom" required>
                  <Input
                    name="prenom"
                    value={formData.prenom}
                    onChange={handleChange}
                    placeholder="Marie"
                    required
                  />
                </FormField>
              </FormGrid>
              <FormGrid>
                <FormField label="Email" required>
                  <Input
                    name="email"
                    type="email"
                    value={formData.email}
                    onChange={handleChange}
                    placeholder="dr.martin@clinique.com"
                    required
                  />
                </FormField>
                <FormField label="Telephone">
                  <Input
                    name="telephone"
                    value={formData.telephone}
                    onChange={handleChange}
                    placeholder="01 23 45 67 89"
                  />
                </FormField>
              </FormGrid>
            </div>
          ),
        },
        {
          title: 'Compte utilisateur',
          description: 'Identifiants de connexion au systeme',
          children: (
            <FormGrid>
              <FormField label="Login" required>
                <Input
                  name="login"
                  value={formData.login}
                  onChange={handleChange}
                  placeholder="dr.martin"
                  required
                />
              </FormField>
              <FormField label="Mot de passe" required>
                <Input
                  name="motDePasse"
                  type="password"
                  value={formData.motDePasse}
                  onChange={handleChange}
                  placeholder="••••••••"
                  required
                />
              </FormField>
            </FormGrid>
          ),
        },
        {
          title: 'Informations professionnelles',
          description: 'Licence, cabinet et specialite',
          children: (
            <div className="space-y-4">
              <FormGrid>
                <FormField label="Numero de licence" required>
                  <Input
                    name="numeroLicence"
                    value={formData.numeroLicence}
                    onChange={handleChange}
                    placeholder="MED-XXXXX"
                    required
                  />
                </FormField>
                <FormField label="Cabinet" required>
                  <Input
                    name="cabinet"
                    value={formData.cabinet}
                    onChange={handleChange}
                    placeholder="Cabinet Medical du Centre"
                    required
                  />
                </FormField>
              </FormGrid>
              <FormField label="Specialite" required>
                <Select
                  value={formData.specialiteId}
                  onValueChange={(value) => setFormData((prev) => ({ ...prev, specialiteId: value }))}
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
      ]}
    />
  );
}
