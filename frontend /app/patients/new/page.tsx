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
  const [error, setError] = useState<string | null>(null);
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
        console.error('Erreur chargement médecins:', error);
      }
    };
    fetchMedecins();
  }, []);

  const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const { name, value } = e.target;
    setFormData((prev) => ({ ...prev, [name]: value }));
    setError(null); // Effacer l'erreur quand l'utilisateur modifie un champ
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setLoading(true);
    setError(null);
    
    try {
      // Nettoyer et préparer les données
      const data = {
        nom: formData.nom.trim(),
        prenom: formData.prenom.trim(),
        email: formData.email.trim().toLowerCase(),
        telephone: formData.telephone.trim(),
        adresse: formData.adresse.trim() || "",
        dateNaissance: formData.dateNaissance || null, // Garder le format YYYY-MM-DD
        numeroSecuriteSociale: formData.numeroSecuriteSociale.trim() || null,
        mutuelle: formData.mutuelle.trim() || null,
        personneContact: formData.personneContact.trim() || null,
        telephoneContact: formData.telephoneContact.trim() || null,
        medecinTraitantId: formData.medecinTraitantId ? parseInt(formData.medecinTraitantId) : null,
      };
      
      console.log('📤 Envoi des données:', JSON.stringify(data, null, 2));
      
      await apiClient.createPatient(data);
      router.push('/patients');
    } catch (error: any) {
      console.error('❌ Erreur création patient:', error);
      setError(error.message || 'Erreur lors de la création du patient. Vérifiez tous les champs.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <FormLayout
      title="Nouveau patient"
      description="Enregistrer un nouveau patient dans le système"
      backHref="/patients"
      onSubmit={handleSubmit}
      isSubmitting={loading}
      submitLabel="Créer le patient"
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
          <h2 className="text-lg font-semibold mb-2">Informations personnelles</h2>
          <p className="text-sm text-gray-500 mb-4">Les informations de base du patient</p>
          <div className="space-y-4">
            <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
              <FormField label="Nom" required>
                <Input
                  name="nom"
                  value={formData.nom}
                  onChange={handleChange}
                  placeholder="Dupont"
                  required
                />
              </FormField>
              <FormField label="Prénom" required>
                <Input
                  name="prenom"
                  value={formData.prenom}
                  onChange={handleChange}
                  placeholder="Jean"
                  required
                />
              </FormField>
            </div>
            
            <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
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
              <FormField label="Téléphone" required>
                <Input
                  name="telephone"
                  value={formData.telephone}
                  onChange={handleChange}
                  placeholder="06 12 34 56 78"
                  required
                />
              </FormField>
            </div>
            
            <FormField label="Adresse">
              <Input
                name="adresse"
                value={formData.adresse}
                onChange={handleChange}
                placeholder="123 Rue de la Santé, 75000 Paris"
              />
            </FormField>
            
            <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
              <FormField label="Date de naissance">
                <Input
                  name="dateNaissance"
                  type="date"
                  value={formData.dateNaissance}
                  onChange={handleChange}
                />
              </FormField>
              <FormField label="Numéro Sécurité Sociale">
                <Input
                  name="numeroSecuriteSociale"
                  value={formData.numeroSecuriteSociale}
                  onChange={handleChange}
                  placeholder="1 23 45 67 890 123 45"
                />
              </FormField>
            </div>
          </div>
        </div>

        {/* Section 2: Informations médicales */}
        <div>
          <h2 className="text-lg font-semibold mb-2">Informations médicales</h2>
          <p className="text-sm text-gray-500 mb-4">Couverture santé et médecin traitant</p>
          <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
            <FormField label="Mutuelle">
              <Input
                name="mutuelle"
                value={formData.mutuelle}
                onChange={handleChange}
                placeholder="Nom de la mutuelle"
              />
            </FormField>
            <FormField label="Médecin traitant">
              <Select
                value={formData.medecinTraitantId}
                onValueChange={(value) => setFormData((prev) => ({ ...prev, medecinTraitantId: value }))}
              >
                <SelectTrigger>
                  <SelectValue placeholder="Sélectionner un médecin" />
                </SelectTrigger>
                <SelectContent>
                  {medecins.length === 0 ? (
                    <SelectItem value="aucun" disabled>Aucun médecin disponible</SelectItem>
                  ) : (
                    medecins.map((m) => (
                      <SelectItem key={m.id} value={String(m.id)}>
                        Dr. {m.prenom} {m.nom}
                      </SelectItem>
                    ))
                  )}
                </SelectContent>
              </Select>
            </FormField>
          </div>
        </div>

        {/* Section 3: Contact d'urgence */}
        <div>
          <h2 className="text-lg font-semibold mb-2">Contact d&apos;urgence</h2>
          <p className="text-sm text-gray-500 mb-4">Personne à contacter en cas d&apos;urgence</p>
          <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
            <FormField label="Nom du contact">
              <Input
                name="personneContact"
                value={formData.personneContact}
                onChange={handleChange}
                placeholder="Nom complet"
              />
            </FormField>
            <FormField label="Téléphone du contact">
              <Input
                name="telephoneContact"
                value={formData.telephoneContact}
                onChange={handleChange}
                placeholder="06 XX XX XX XX"
              />
            </FormField>
          </div>
        </div>
      </div>
    </FormLayout>
  );
}