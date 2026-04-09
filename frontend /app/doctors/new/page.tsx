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
  const [error, setError] = useState<string | null>(null);
  const [specialities, setSpecialities] = useState<Specialite[]>([]);
  const [formData, setFormData] = useState({
    nom: '',
    prenom: '',
    email: '',
    login: '',
    password: '',           // ← Changé : motDePasse → password
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
    setError(null);
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setLoading(true);
    setError(null);
    
    try {
      // Validation
      if (!formData.nom.trim()) throw new Error('Le nom est obligatoire');
      if (!formData.prenom.trim()) throw new Error('Le prénom est obligatoire');
      if (!formData.email.trim()) throw new Error('L\'email est obligatoire');
      if (!formData.login.trim()) throw new Error('Le login est obligatoire');
      if (!formData.password.trim()) throw new Error('Le mot de passe est obligatoire');
      
      // Préparer les données pour l'API (les noms doivent correspondre au DTO .NET)
      const data = {
        nom: formData.nom.trim(),
        prenom: formData.prenom.trim(),
        email: formData.email.trim(),
        login: formData.login.trim(),
        password: formData.password,           // ← Important : password (pas motDePasse)
        numeroLicence: formData.numeroLicence.trim() || null,
        cabinet: formData.cabinet.trim() || null,
        specialiteId: formData.specialiteId ? parseInt(formData.specialiteId) : null,
        joursDisponibles: []  // Liste vide par défaut
      };
      
      console.log('📤 Envoi des données médecin:', JSON.stringify(data, null, 2));
      
      await apiClient.createMedecin(data);
      router.push('/doctors');
    } catch (error: any) {
      console.error('❌ Erreur création médecin:', error);
      setError(error.message || 'Erreur lors de la création du médecin');
    } finally {
      setLoading(false);
    }
  };

  return (
    <FormLayout
      title="Nouveau médecin"
      description="Enregistrer un nouveau médecin dans le système"
      backHref="/doctors"
      onSubmit={handleSubmit}
      isSubmitting={loading}
      submitLabel="Créer le médecin"
    >
      {error && (
        <div className="mb-6 p-4 bg-red-50 border border-red-200 rounded-lg text-red-700">
          <p className="font-semibold">Erreur :</p>
          <p>{error}</p>
        </div>
      )}
      
      <div className="space-y-8">
        {/* Section 1: Informations personnelles */}
        <div>
          <h2 className="text-lg font-semibold mb-4">Informations personnelles</h2>
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
              <FormField label="Prénom" required>
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
              <FormField label="Téléphone">
                <Input
                  name="telephone"
                  value={formData.telephone}
                  onChange={handleChange}
                  placeholder="01 23 45 67 89"
                />
              </FormField>
            </FormGrid>
          </div>
        </div>

        {/* Section 2: Compte utilisateur */}
        <div>
          <h2 className="text-lg font-semibold mb-4">Compte utilisateur</h2>
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
                name="password"
                type="password"
                value={formData.password}
                onChange={handleChange}
                placeholder="••••••••"
                required
              />
            </FormField>
          </FormGrid>
        </div>

        {/* Section 3: Informations professionnelles */}
        <div>
          <h2 className="text-lg font-semibold mb-4">Informations professionnelles</h2>
          <div className="space-y-4">
            <FormGrid>
              <FormField label="Numéro de licence">
                <Input
                  name="numeroLicence"
                  value={formData.numeroLicence}
                  onChange={handleChange}
                  placeholder="MED-XXXXX"
                />
              </FormField>
              <FormField label="Cabinet">
                <Input
                  name="cabinet"
                  value={formData.cabinet}
                  onChange={handleChange}
                  placeholder="Cabinet Médical du Centre"
                />
              </FormField>
            </FormGrid>
            <FormField label="Spécialité">
              <Select
                value={formData.specialiteId}
                onValueChange={(value) => setFormData((prev) => ({ ...prev, specialiteId: value }))}
              >
                <SelectTrigger>
                  <SelectValue placeholder="Sélectionner une spécialité" />
                </SelectTrigger>
                <SelectContent>
                  {specialities.length === 0 ? (
                    <SelectItem value="aucun" disabled>Aucune spécialité disponible</SelectItem>
                  ) : (
                    specialities.map((s) => (
                      <SelectItem key={s.id} value={String(s.id)}>
                        {s.nom}
                      </SelectItem>
                    ))
                  )}
                </SelectContent>
              </Select>
            </FormField>
          </div>
        </div>
      </div>
    </FormLayout>
  );
}