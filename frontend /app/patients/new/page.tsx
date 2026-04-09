'use client';

import { useState, useEffect } from 'react';
import { useRouter } from 'next/navigation';
import { apiClient, Medecin, Patient } from '@/lib/api-client';
import { FormLayout, FormField, FormGrid } from '@/components/form-layout';
import { Input } from '@/components/ui/input';
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from '@/components/ui/select';

export default function NewPatientPage() {
  const router = useRouter();
  const [loading, setLoading] = useState(false);
  const [medecins, setMedecins] = useState<Medecin[]>([]);
  const [formData, setFormData] = useState({
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
    medecinTraitantId: '',
  });

  useEffect(() => {
    const fetchMedecins = async () => {
      try {
        const data = await apiClient.getMedecins();
        setMedecins(data);
      } catch (error) {
        console.error('Failed to fetch doctors:', error);
      }
    };
    fetchMedecins();
  }, []);

  const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const { name, value } = e.target;
    setFormData((prev) => ({ ...prev, [name]: value }));
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setLoading(true);
    try {
      const data: Partial<Patient> = {
        ...formData,
        medecinTraitantId: formData.medecinTraitantId ? parseInt(formData.medecinTraitantId) : undefined,
      };
      await apiClient.createPatient(data);
      router.push('/patients');
    } catch (error) {
      console.error('Failed to create patient:', error);
    } finally {
      setLoading(false);
    }
  };

  return (
    <FormLayout
      title="Nouveau patient"
      description="Enregistrer un nouveau patient dans le systeme"
      backHref="/patients"
      onSubmit={handleSubmit}
      isSubmitting={loading}
      submitLabel="Creer le patient"
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
                    value={formData.nom}
                    onChange={handleChange}
                    placeholder="Dupont"
                    required
                  />
                </FormField>
                <FormField label="Prenom" required>
                  <Input
                    name="prenom"
                    value={formData.prenom}
                    onChange={handleChange}
                    placeholder="Jean"
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
                    placeholder="jean.dupont@email.com"
                    required
                  />
                </FormField>
                <FormField label="Telephone" required>
                  <Input
                    name="telephone"
                    value={formData.telephone}
                    onChange={handleChange}
                    placeholder="06 12 34 56 78"
                    required
                  />
                </FormField>
              </FormGrid>
              <FormField label="Adresse">
                <Input
                  name="adresse"
                  value={formData.adresse}
                  onChange={handleChange}
                  placeholder="123 Rue de la Sante, 75000 Paris"
                />
              </FormField>
              <FormGrid>
                <FormField label="Date de naissance">
                  <Input
                    name="dateNaissance"
                    type="date"
                    value={formData.dateNaissance}
                    onChange={handleChange}
                  />
                </FormField>
                <FormField label="Numero Securite Sociale">
                  <Input
                    name="numeroSecuriteSociale"
                    value={formData.numeroSecuriteSociale}
                    onChange={handleChange}
                    placeholder="1 XX XX XX XXX XXX XX"
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
                  value={formData.mutuelle}
                  onChange={handleChange}
                  placeholder="Nom de la mutuelle"
                />
              </FormField>
              <FormField label="Medecin traitant">
                <Select
                  value={formData.medecinTraitantId}
                  onValueChange={(value) => setFormData((prev) => ({ ...prev, medecinTraitantId: value }))}
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
                  value={formData.personneContact}
                  onChange={handleChange}
                  placeholder="Nom complet"
                />
              </FormField>
              <FormField label="Telephone du contact">
                <Input
                  name="telephoneContact"
                  value={formData.telephoneContact}
                  onChange={handleChange}
                  placeholder="06 XX XX XX XX"
                />
              </FormField>
            </FormGrid>
          ),
        },
      ]}
    />
  );
}
