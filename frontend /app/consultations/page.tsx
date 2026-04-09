'use client';

import { useEffect, useState } from 'react';
import Link from 'next/link';
import { useAuth } from '@/lib/auth-context';
import { apiClient, Consultation } from '@/lib/api-client';
import { AppLayout } from '@/components/app-layout';
import { Button } from '@/components/ui/button';
import { Card } from '@/components/ui/card';
import { Badge } from '@/components/ui/badge';
import { Avatar, AvatarFallback } from '@/components/ui/avatar';
import { Skeleton } from '@/components/ui/skeleton';
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuTrigger,
} from '@/components/ui/dropdown-menu';
import {
  AlertDialog,
  AlertDialogAction,
  AlertDialogCancel,
  AlertDialogContent,
  AlertDialogDescription,
  AlertDialogFooter,
  AlertDialogHeader,
  AlertDialogTitle,
} from '@/components/ui/alert-dialog';
import {
  Plus,
  MoreHorizontal,
  Pencil,
  Trash2,
  ClipboardList,
  ChevronLeft,
  ChevronRight,
  Calendar,
  FileText,
} from 'lucide-react';

export default function ConsultationsPage() {
  const { user } = useAuth();
  const [consultations, setConsultations] = useState<Consultation[]>([]);
  const [loading, setLoading] = useState(true);
  const [currentPage, setCurrentPage] = useState(1);
  const [deleteId, setDeleteId] = useState<number | null>(null);
  const itemsPerPage = 10;

  useEffect(() => {
    const fetchConsultations = async () => {
      try {
        let data: Consultation[];
        if (user?.role === 'ADMIN') {
          data = await apiClient.getConsultations();
        } else if (user?.role === 'MEDECIN') {
          data = await apiClient.getConsultationsByMedecin(user.id);
        } else {
          data = [];
        }
        setConsultations(data);
      } catch (error) {
        console.error('Failed to fetch consultations:', error);
      } finally {
        setLoading(false);
      }
    };

    if (user) {
      fetchConsultations();
    }
  }, [user]);

  const totalPages = Math.ceil(consultations.length / itemsPerPage);
  const startIndex = (currentPage - 1) * itemsPerPage;
  const paginatedConsultations = consultations.slice(startIndex, startIndex + itemsPerPage);

  const handleDelete = async () => {
    if (!deleteId) return;
    try {
      await apiClient.deleteConsultation(deleteId);
      setConsultations(consultations.filter((c) => c.id !== deleteId));
      setDeleteId(null);
    } catch (error) {
      console.error('Failed to delete consultation:', error);
    }
  };

  const getStatusColor = (status: string) => {
    switch (status.toLowerCase()) {
      case 'terminee':
        return 'bg-success/10 text-success border-success/20';
      case 'en_cours':
        return 'bg-primary/10 text-primary border-primary/20';
      case 'annulee':
        return 'bg-destructive/10 text-destructive border-destructive/20';
      default:
        return 'bg-secondary text-secondary-foreground';
    }
  };

  return (
    <AppLayout
      title={user?.role === 'MEDECIN' ? 'Mes consultations' : 'Consultations'}
      description="Gerez les consultations medicales"
      actions={
        user?.role === 'ADMIN' ? (
          <Link href="/consultations/new">
            <Button className="gap-2">
              <Plus className="h-4 w-4" />
              Nouvelle consultation
            </Button>
          </Link>
        ) : undefined
      }
    >
      {/* Table */}
      <Card className="overflow-hidden">
        <div className="overflow-x-auto">
          <table className="w-full">
            <thead>
              <tr className="border-b bg-muted/50">
                <th className="px-4 py-3 text-left text-xs font-semibold uppercase tracking-wider text-muted-foreground">
                  Patient
                </th>
                <th className="px-4 py-3 text-left text-xs font-semibold uppercase tracking-wider text-muted-foreground">
                  Medecin
                </th>
                <th className="px-4 py-3 text-left text-xs font-semibold uppercase tracking-wider text-muted-foreground">
                  Date
                </th>
                <th className="px-4 py-3 text-left text-xs font-semibold uppercase tracking-wider text-muted-foreground">
                  Motif
                </th>
                <th className="px-4 py-3 text-left text-xs font-semibold uppercase tracking-wider text-muted-foreground">
                  Diagnostic
                </th>
                <th className="px-4 py-3 text-left text-xs font-semibold uppercase tracking-wider text-muted-foreground">
                  Statut
                </th>
                <th className="px-4 py-3 text-right text-xs font-semibold uppercase tracking-wider text-muted-foreground">
                  Actions
                </th>
              </tr>
            </thead>
            <tbody className="divide-y divide-border">
              {loading ? (
                [...Array(5)].map((_, i) => (
                  <tr key={i}>
                    <td className="px-4 py-3">
                      <div className="flex items-center gap-3">
                        <Skeleton className="h-9 w-9 rounded-full" />
                        <Skeleton className="h-4 w-32" />
                      </div>
                    </td>
                    <td className="px-4 py-3"><Skeleton className="h-4 w-32" /></td>
                    <td className="px-4 py-3"><Skeleton className="h-4 w-24" /></td>
                    <td className="px-4 py-3"><Skeleton className="h-4 w-32" /></td>
                    <td className="px-4 py-3"><Skeleton className="h-4 w-40" /></td>
                    <td className="px-4 py-3"><Skeleton className="h-6 w-20" /></td>
                    <td className="px-4 py-3"><Skeleton className="h-8 w-8 ml-auto" /></td>
                  </tr>
                ))
              ) : paginatedConsultations.length === 0 ? (
                <tr>
                  <td colSpan={7} className="py-12">
                    <div className="flex flex-col items-center justify-center text-center">
                      <ClipboardList className="h-12 w-12 text-muted-foreground/50" />
                      <p className="mt-3 text-sm text-muted-foreground">
                        Aucune consultation enregistree
                      </p>
                      {user?.role === 'ADMIN' && (
                        <Link href="/consultations/new" className="mt-4">
                          <Button variant="outline" size="sm">
                            Creer une consultation
                          </Button>
                        </Link>
                      )}
                    </div>
                  </td>
                </tr>
              ) : (
                paginatedConsultations.map((consultation) => (
                  <tr key={consultation.id} className="group transition-colors hover:bg-muted/50">
                    <td className="px-4 py-3">
                      <div className="flex items-center gap-3">
                        <Avatar className="h-9 w-9">
                          <AvatarFallback className="bg-primary/10 text-primary text-sm font-medium">
                            {consultation.patientPrenom[0]}{consultation.patientNom[0]}
                          </AvatarFallback>
                        </Avatar>
                        <span className="font-medium text-foreground">
                          {consultation.patientPrenom} {consultation.patientNom}
                        </span>
                      </div>
                    </td>
                    <td className="px-4 py-3 text-sm text-muted-foreground">
                      Dr. {consultation.medecinPrenom} {consultation.medecinNom}
                    </td>
                    <td className="px-4 py-3">
                      <div className="flex items-center gap-1.5 text-sm text-foreground">
                        <Calendar className="h-3.5 w-3.5 text-muted-foreground" />
                        {new Date(consultation.dateConsultation).toLocaleDateString('fr-FR')}
                      </div>
                    </td>
                    <td className="px-4 py-3">
                      <p className="text-sm text-foreground max-w-[150px] truncate">
                        {consultation.motif}
                      </p>
                    </td>
                    <td className="px-4 py-3">
                      <div className="flex items-center gap-1.5 text-sm text-muted-foreground max-w-[180px]">
                        <FileText className="h-3.5 w-3.5 shrink-0" />
                        <span className="truncate">{consultation.diagnostic || 'Non defini'}</span>
                      </div>
                    </td>
                    <td className="px-4 py-3">
                      <Badge variant="outline" className={getStatusColor(consultation.statut)}>
                        {consultation.statut}
                      </Badge>
                    </td>
                    <td className="px-4 py-3 text-right">
                      <DropdownMenu>
                        <DropdownMenuTrigger asChild>
                          <Button variant="ghost" size="icon" className="h-8 w-8">
                            <MoreHorizontal className="h-4 w-4" />
                          </Button>
                        </DropdownMenuTrigger>
                        <DropdownMenuContent align="end">
                          <DropdownMenuItem asChild>
                            <Link href={`/consultations/${consultation.id}/edit`} className="flex items-center gap-2">
                              <Pencil className="h-4 w-4" />
                              Modifier
                            </Link>
                          </DropdownMenuItem>
                          {user?.role === 'ADMIN' && (
                            <DropdownMenuItem
                              onClick={() => setDeleteId(consultation.id)}
                              className="text-destructive focus:text-destructive"
                            >
                              <Trash2 className="h-4 w-4 mr-2" />
                              Supprimer
                            </DropdownMenuItem>
                          )}
                        </DropdownMenuContent>
                      </DropdownMenu>
                    </td>
                  </tr>
                ))
              )}
            </tbody>
          </table>
        </div>

        {/* Pagination */}
        {totalPages > 1 && (
          <div className="flex items-center justify-between border-t px-4 py-3">
            <p className="text-sm text-muted-foreground">
              Affichage de {startIndex + 1} a {Math.min(startIndex + itemsPerPage, consultations.length)} sur {consultations.length}
            </p>
            <div className="flex items-center gap-2">
              <Button
                variant="outline"
                size="sm"
                onClick={() => setCurrentPage((p) => Math.max(1, p - 1))}
                disabled={currentPage === 1}
              >
                <ChevronLeft className="h-4 w-4" />
              </Button>
              <span className="text-sm text-muted-foreground">
                Page {currentPage} sur {totalPages}
              </span>
              <Button
                variant="outline"
                size="sm"
                onClick={() => setCurrentPage((p) => Math.min(totalPages, p + 1))}
                disabled={currentPage === totalPages}
              >
                <ChevronRight className="h-4 w-4" />
              </Button>
            </div>
          </div>
        )}
      </Card>

      {/* Delete Confirmation */}
      <AlertDialog open={deleteId !== null} onOpenChange={() => setDeleteId(null)}>
        <AlertDialogContent>
          <AlertDialogHeader>
            <AlertDialogTitle>Confirmer la suppression</AlertDialogTitle>
            <AlertDialogDescription>
              Etes-vous sur de vouloir supprimer cette consultation ? Cette action est irreversible.
            </AlertDialogDescription>
          </AlertDialogHeader>
          <AlertDialogFooter>
            <AlertDialogCancel>Annuler</AlertDialogCancel>
            <AlertDialogAction onClick={handleDelete} className="bg-destructive text-destructive-foreground hover:bg-destructive/90">
              Supprimer
            </AlertDialogAction>
          </AlertDialogFooter>
        </AlertDialogContent>
      </AlertDialog>
    </AppLayout>
  );
}
