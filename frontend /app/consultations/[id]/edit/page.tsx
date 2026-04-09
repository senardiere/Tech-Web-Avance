'use client';

import { useEffect, useState } from 'react';
import { useParams, useRouter } from 'next/navigation';
import { apiClient, Consultation, Patient, Medecin } from '@/lib/api-client';
import { Button } from '@/components/ui/button';
import { Card } from '@/components/ui/card';
import { Input } from '@/components/ui/input';
import Link from 'next/link';

export default function EditConsultationPage() {
  const params = useParams();
  const router = useRouter();
  const consultationId = parseInt(params.id as string);
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [patients, setPatients] = useState<Patient[]>([]);
  const [medecins, setMedecins] = useState<Medecin[]>([]);
  const [formData, setFormData] = useState<Partial<Consultation>>({
    patientId: undefined,
    medecinId: undefined,
    motif: '',
    diagnostic: '',
    prescription: '',
    montant: 0,
  });

  useEffect(() => {
    const fetchData = async () => {
      try {
        const [consultationData, patientsData, medecinsData] = await Promise.all([
          apiClient.getConsultationById(consultationId),
          apiClient.getPatients(),
          apiClient.getMedecins(),
        ]);
        setFormData(consultationData);
        setPatients(patientsData);
        setMedecins(medecinsData);
      } catch (error) {
        console.error('Failed to fetch data:', error);
      } finally {
        setLoading(false);
      }
    };

    fetchData();
  }, [consultationId]);

  const handleChange = (e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement | HTMLTextAreaElement>) => {
    const { name, value } = e.target;
    setFormData((prev) => ({
      ...prev,
      [name]: name === 'patientId' || name === 'medecinId'
        ? parseInt(value)
        : name === 'montant'
        ? parseFloat(value)
        : value,
    }));
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setSaving(true);
    try {
      await apiClient.updateConsultation(consultationId, formData);
      router.push('/consultations');
    } catch (error) {
      console.error('Failed to update consultation:', error);
      alert('Erreur lors de la modification');
    } finally {
      setSaving(false);
    }
  };

  if (loading) {
    return <div className="flex items-center justify-center min-h-screen">Chargement...</div>;
  }

  return (
    <div className="min-h-screen bg-gray-50">
      <div className="max-w-2xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        <div className="mb-6">
          <h1 className="text-3xl font-bold text-gray-900">Modifier la consultation</h1>
        </div>

        <Card className="p-8">
          <form onSubmit={handleSubmit} className="space-y-6">
            <div className="grid grid-cols-2 gap-4">
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-2">Patient *</label>
                <select
                  name="patientId"
                  value={formData.patientId || ''}
                  onChange={handleChange}
                  required
                  className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
                >
                  <option value="">Sélectionner un patient</option>
                  {patients.map((p) => (
                    <option key={p.id} value={p.id}>
                      {p.prenom} {p.nom}
                    </option>
                  ))}
                </select>
              </div>

              <div>
                <label className="block text-sm font-medium text-gray-700 mb-2">Médecin *</label>
                <select
                  name="medecinId"
                  value={formData.medecinId || ''}
                  onChange={handleChange}
                  required
                  className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
                >
                  <option value="">Sélectionner un médecin</option>
                  {medecins.map((m) => (
                    <option key={m.id} value={m.id}>
                      {m.prenom} {m.nom}
                    </option>
                  ))}
                </select>
              </div>
            </div>

            <div>
              <label className="block text-sm font-medium text-gray-700 mb-2">Motif *</label>
              <Input
                name="motif"
                value={formData.motif || ''}
                onChange={handleChange}
                required
              />
            </div>

            <div>
              <label className="block text-sm font-medium text-gray-700 mb-2">Diagnostic *</label>
              <Input
                name="diagnostic"
                value={formData.diagnostic || ''}
                onChange={handleChange}
                required
              />
            </div>

            <div>
              <label className="block text-sm font-medium text-gray-700 mb-2">Prescription</label>
              <textarea
                name="prescription"
                value={formData.prescription || ''}
                onChange={handleChange}
                rows={3}
                className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
              />
            </div>

            <div>
              <label className="block text-sm font-medium text-gray-700 mb-2">Montant</label>
              <Input
                name="montant"
                type="number"
                min="0"
                step="0.01"
                value={formData.montant || ''}
                onChange={handleChange}
              />
            </div>

            <div className="flex gap-4 pt-6">
              <Button type="submit" disabled={saving}>
                {saving ? 'Sauvegarde...' : 'Enregistrer'}
              </Button>
              <Link href="/consultations">
                <Button variant="outline">Annuler</Button>
              </Link>
            </div>
          </form>
        </Card>
      </div>
    </div>
  );
}
