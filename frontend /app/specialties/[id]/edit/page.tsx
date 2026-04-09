'use client';

import { useEffect, useState } from 'react';
import { useParams, useRouter } from 'next/navigation';
import { apiClient, Specialite } from '@/lib/api-client';
import { Button } from '@/components/ui/button';
import { Card } from '@/components/ui/card';
import { Input } from '@/components/ui/input';
import Link from 'next/link';

export default function EditSpecialtyPage() {
  const params = useParams();
  const router = useRouter();
  const specialtyId = parseInt(params.id as string);
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [formData, setFormData] = useState<Partial<Specialite>>({
    code: '',
    nom: '',
    description: '',
  });

  useEffect(() => {
    const fetchSpecialty = async () => {
      try {
        const data = await apiClient.getSpecialiteById(specialtyId);
        setFormData(data);
      } catch (error) {
        console.error('Failed to fetch specialty:', error);
      } finally {
        setLoading(false);
      }
    };

    fetchSpecialty();
  }, [specialtyId]);

  const handleChange = (e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement>) => {
    const { name, value } = e.target;
    setFormData((prev) => ({
      ...prev,
      [name]: value,
    }));
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setSaving(true);
    try {
      await apiClient.updateSpecialite(specialtyId, formData);
      router.push('/specialties');
    } catch (error) {
      console.error('Failed to update specialty:', error);
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
          <h1 className="text-3xl font-bold text-gray-900">Modifier {formData.nom}</h1>
        </div>

        <Card className="p-8">
          <form onSubmit={handleSubmit} className="space-y-6">
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-2">Code *</label>
              <Input
                name="code"
                value={formData.code || ''}
                onChange={handleChange}
                required
              />
            </div>

            <div>
              <label className="block text-sm font-medium text-gray-700 mb-2">Nom / Libellé *</label>
              <Input
                name="nom"
                value={formData.nom || ''}
                onChange={handleChange}
                required
              />
            </div>

            <div>
              <label className="block text-sm font-medium text-gray-700 mb-2">Description</label>
              <textarea
                name="description"
                value={formData.description || ''}
                onChange={handleChange}
                rows={4}
                className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
              />
            </div>

            <div className="flex gap-4 pt-6">
              <Button type="submit" disabled={saving}>
                {saving ? 'Sauvegarde...' : 'Enregistrer'}
              </Button>
              <Link href="/specialties">
                <Button variant="outline">Annuler</Button>
              </Link>
            </div>
          </form>
        </Card>
      </div>
    </div>
  );
}
