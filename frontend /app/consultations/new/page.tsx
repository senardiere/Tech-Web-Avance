'use client';

import { useState, useEffect } from 'react';
import { useRouter } from 'next/navigation';
import { apiClient, Patient, Medecin } from '@/lib/api-client';
import { AppLayout } from '@/components/app-layout';
import { Button } from '@/components/ui/button';
import { Card } from '@/components/ui/card';
import { Input } from '@/components/ui/input';
import { Label } from '@/components/ui/label';
import { Textarea } from '@/components/ui/textarea';
import Link from 'next/link';
import { ArrowLeft } from 'lucide-react';

export default function NewConsultationPage() {
  const router = useRouter();
  const [loading, setLoading] = useState(false);
  const [patients, setPatients] = useState<Patient[]>([]);
  const [medecins, setMedecins] = useState<Medecin[]>([]);
  const [dataLoading, setDataLoading] = useState(true);

  const [formData, setFormData] = useState({
    patientId: '',
    medecinId: '',
    dateConsultation: '',
    motif: '',
    diagnostic: '',
    prescription: '',
    montant: '',
    poids: '',
    taille: '',
    tension: ''
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

  const handleChange = (e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement | HTMLTextAreaElement>) => {
    const { name, value } = e.target;
    setFormData((prev) => ({
      ...prev,
      [name]: value,
    }));
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setLoading(true);
    
    try {
      // Validation des champs obligatoires
      if (!formData.patientId) {
        alert('Veuillez sélectionner un patient');
        setLoading(false);
        return;
      }
      if (!formData.medecinId) {
        alert('Veuillez sélectionner un médecin');
        setLoading(false);
        return;
      }
      if (!formData.dateConsultation) {
        alert('Veuillez saisir la date de consultation');
        setLoading(false);
        return;
      }
      if (!formData.motif.trim()) {
        alert('Veuillez saisir le motif de la consultation');
        setLoading(false);
        return;
      }
      if (!formData.diagnostic.trim()) {
        alert('Veuillez saisir le diagnostic');
        setLoading(false);
        return;
      }

      // Formater la date sans timezone (format local)
      const dateObj = new Date(formData.dateConsultation);
      const year = dateObj.getFullYear();
      const month = String(dateObj.getMonth() + 1).padStart(2, '0');
      const day = String(dateObj.getDate()).padStart(2, '0');
      const hours = String(dateObj.getHours()).padStart(2, '0');
      const minutes = String(dateObj.getMinutes()).padStart(2, '0');
      const seconds = String(dateObj.getSeconds()).padStart(2, '0');
      
      // Format: 2026-04-10T09:00:00
      const formattedDate = `${year}-${month}-${day}T${hours}:${minutes}:${seconds}`;
      
      // Préparation des données
      const data = {
        patientId: parseInt(formData.patientId),
        medecinId: parseInt(formData.medecinId),
        dateConsultation: formattedDate,
        motif: formData.motif.trim(),
        diagnostic: formData.diagnostic.trim(),
        prescription: formData.prescription.trim() || "",
        observations: "",
        montant: formData.montant ? parseFloat(formData.montant) : 0,
        poids: formData.poids ? parseFloat(formData.poids) : null,
        taille: formData.taille ? parseInt(formData.taille) : null,
        tension: formData.tension.trim() || ""
      };

      console.log('📤 Envoi consultation:', JSON.stringify(data, null, 2));
      
      const response = await apiClient.createConsultation(data);
      console.log('✅ Consultation créée avec succès:', response);
      
      router.push('/consultations');
    } catch (error: any) {
      console.error('Erreur détaillée:', error);
      
      let errorMessage = 'Erreur lors de la création de la consultation';
      if (error.message) {
        errorMessage += `: ${error.message}`;
      }
      
      alert(errorMessage);
    } finally {
      setLoading(false);
    }
  };

  if (dataLoading) {
    return (
      <AppLayout title="Nouvelle consultation" description="Créer une nouvelle consultation">
        <div className="flex items-center justify-center min-h-[400px]">
          <div className="text-center">
            <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-primary mx-auto"></div>
            <p className="mt-4 text-muted-foreground">Chargement des données...</p>
          </div>
        </div>
      </AppLayout>
    );
  }

  return (
    <AppLayout
      title="Nouvelle consultation"
      description="Créer une nouvelle consultation médicale"
    >
      <div className="max-w-3xl mx-auto">
        <div className="mb-6">
          <Link href="/consultations">
            <Button variant="ghost" size="sm" className="gap-2">
              <ArrowLeft className="h-4 w-4" />
              Retour à la liste
            </Button>
          </Link>
        </div>

        <Card className="p-6">
          <form onSubmit={handleSubmit} className="space-y-6">
            {/* Patient et Médecin */}
            <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
              <div className="space-y-2">
                <Label htmlFor="patientId" className="font-semibold">
                  Patient <span className="text-red-500">*</span>
                </Label>
                <select
                  id="patientId"
                  name="patientId"
                  value={formData.patientId}
                  onChange={handleChange}
                  required
                  className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-primary focus:border-transparent"
                >
                  <option value="">Sélectionner un patient</option>
                  {patients.map((p) => (
                    <option key={p.id} value={p.id}>
                      {p.prenom} {p.nom}
                    </option>
                  ))}
                </select>
              </div>

              <div className="space-y-2">
                <Label htmlFor="medecinId" className="font-semibold">
                  Médecin <span className="text-red-500">*</span>
                </Label>
                <select
                  id="medecinId"
                  name="medecinId"
                  value={formData.medecinId}
                  onChange={handleChange}
                  required
                  className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-primary focus:border-transparent"
                >
                  <option value="">Sélectionner un médecin</option>
                  {medecins.map((m) => (
                    <option key={m.id} value={m.id}>
                      Dr. {m.prenom} {m.nom}
                    </option>
                  ))}
                </select>
              </div>
            </div>

            {/* Date de consultation */}
            <div className="space-y-2">
              <Label htmlFor="dateConsultation" className="font-semibold">
                Date et heure de consultation <span className="text-red-500">*</span>
              </Label>
              <Input
                id="dateConsultation"
                name="dateConsultation"
                type="datetime-local"
                value={formData.dateConsultation}
                onChange={handleChange}
                required
                className="w-full"
              />
              <p className="text-xs text-muted-foreground">
                Sélectionnez la date et l'heure de la consultation
              </p>
            </div>

            {/* Motif */}
            <div className="space-y-2">
              <Label htmlFor="motif" className="font-semibold">
                Motif de la consultation <span className="text-red-500">*</span>
              </Label>
              <Input
                id="motif"
                name="motif"
                value={formData.motif}
                onChange={handleChange}
                required
                placeholder="Ex: Consultation de routine, Douleur thoracique, Suivi diabète..."
                className="w-full"
              />
            </div>

            {/* Diagnostic */}
            <div className="space-y-2">
              <Label htmlFor="diagnostic" className="font-semibold">
                Diagnostic <span className="text-red-500">*</span>
              </Label>
              <Textarea
                id="diagnostic"
                name="diagnostic"
                value={formData.diagnostic}
                onChange={handleChange}
                rows={3}
                required
                placeholder="Description du diagnostic établi..."
                className="w-full"
              />
            </div>

            {/* Prescription */}
            <div className="space-y-2">
              <Label htmlFor="prescription">Prescription médicale</Label>
              <Textarea
                id="prescription"
                name="prescription"
                value={formData.prescription}
                onChange={handleChange}
                rows={3}
                placeholder="Médicaments prescrits, posologie, durée du traitement..."
                className="w-full"
              />
            </div>

            {/* Informations complémentaires */}
            <div className="space-y-4">
              <Label className="font-semibold">Informations complémentaires</Label>
              <div className="grid grid-cols-1 md:grid-cols-4 gap-4">
                <div className="space-y-2">
                  <Label htmlFor="montant">Montant (€)</Label>
                  <Input
                    id="montant"
                    name="montant"
                    type="number"
                    min="0"
                    step="0.01"
                    value={formData.montant}
                    onChange={handleChange}
                    placeholder="0.00"
                    className="w-full"
                  />
                </div>

                <div className="space-y-2">
                  <Label htmlFor="poids">Poids (kg)</Label>
                  <Input
                    id="poids"
                    name="poids"
                    type="number"
                    step="0.1"
                    min="0"
                    value={formData.poids}
                    onChange={handleChange}
                    placeholder="70.5"
                    className="w-full"
                  />
                </div>

                <div className="space-y-2">
                  <Label htmlFor="taille">Taille (cm)</Label>
                  <Input
                    id="taille"
                    name="taille"
                    type="number"
                    min="0"
                    value={formData.taille}
                    onChange={handleChange}
                    placeholder="175"
                    className="w-full"
                  />
                </div>

                <div className="space-y-2">
                  <Label htmlFor="tension">Tension artérielle</Label>
                  <Input
                    id="tension"
                    name="tension"
                    value={formData.tension}
                    onChange={handleChange}
                    placeholder="12/8"
                    className="w-full"
                  />
                </div>
              </div>
            </div>

            {/* Boutons */}
            <div className="flex gap-4 pt-4">
              <Button type="submit" disabled={loading}>
                {loading ? 'Création en cours...' : 'Créer la consultation'}
              </Button>
              <Link href="/consultations">
                <Button variant="outline" type="button">
                  Annuler
                </Button>
              </Link>
            </div>
          </form>
        </Card>
      </div>
    </AppLayout>
  );
}