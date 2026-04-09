'use client';

import { useState } from 'react';
import { useRouter } from 'next/navigation';
import { apiClient, Specialite } from '@/lib/api-client';
import { Button } from '@/components/ui/button';
import { Card } from '@/components/ui/card';
import { Input } from '@/components/ui/input';
import Link from 'next/link';

export default function NewSpecialtyPage() {
  const router = useRouter();
  const [loading, setLoading] = useState(false);
  const [formData, setFormData] = useState<Partial<Specialite>>({
    code: '',
    nom: '',
    description: '',
  });

  const handleChange = (e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement>) => {
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
      await apiClient.createSpecialite(formData);
      router.push('/specialties');
    } catch (error) {
      console.error('Failed to create specialty:', error);
      alert('Erreur lors de la création de la spécialité');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="min-h-screen bg-gray-50">
      <div className="max-w-2xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        <div className="mb-6">
          <h1 className="text-3xl font-bold text-gray-900">Nouvelle spécialité</h1>
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
                placeholder="Ex: CAR, NEU, etc..."
              />
            </div>

            <div>
              <label className="block text-sm font-medium text-gray-700 mb-2">Nom / Libellé *</label>
              <Input
                name="nom"
                value={formData.nom || ''}
                onChange={handleChange}
                required
                placeholder="Ex: Cardiologie"
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
                placeholder="Description de la spécialité"
              />
            </div>

            <div className="flex gap-4 pt-6">
              <Button type="submit" disabled={loading}>
                {loading ? 'Création...' : 'Créer la spécialité'}
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
