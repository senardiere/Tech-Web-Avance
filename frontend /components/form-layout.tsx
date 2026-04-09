'use client';

import { ReactNode } from 'react';
import Link from 'next/link';
import { AppLayout } from '@/components/app-layout';
import { Button } from '@/components/ui/button';
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/card';
import { Spinner } from '@/components/ui/spinner';

interface FormLayoutProps {
  title: string;
  description?: string;
  backHref: string;
  backLabel?: string;
  onSubmit: (e: React.FormEvent) => void;
  isSubmitting?: boolean;
  submitLabel?: string;
  children: ReactNode;
  sections?: {
    title: string;
    description?: string;
    children: ReactNode;
  }[];
}

export function FormLayout({
  title,
  description,
  backHref,
  backLabel = 'Annuler',
  onSubmit,
  isSubmitting = false,
  submitLabel = 'Enregistrer',
  children,
  sections,
}: FormLayoutProps) {
  return (
    <AppLayout title={title} description={description}>
      <form onSubmit={onSubmit} className="mx-auto max-w-3xl space-y-6">
        {sections ? (
          sections.map((section, idx) => (
            <Card key={idx}>
              <CardHeader>
                <CardTitle className="text-lg">{section.title}</CardTitle>
                {section.description && (
                  <CardDescription>{section.description}</CardDescription>
                )}
              </CardHeader>
              <CardContent>{section.children}</CardContent>
            </Card>
          ))
        ) : (
          <Card>
            <CardContent className="pt-6">{children}</CardContent>
          </Card>
        )}

        {/* Actions */}
        <div className="flex items-center justify-end gap-3">
          <Link href={backHref}>
            <Button type="button" variant="outline">
              {backLabel}
            </Button>
          </Link>
          <Button type="submit" disabled={isSubmitting}>
            {isSubmitting ? (
              <>
                <Spinner className="mr-2 h-4 w-4" />
                En cours...
              </>
            ) : (
              submitLabel
            )}
          </Button>
        </div>
      </form>
    </AppLayout>
  );
}

// Field component for consistent styling
interface FormFieldProps {
  label: string;
  required?: boolean;
  error?: string;
  children: ReactNode;
  className?: string;
}

export function FormField({ label, required, error, children, className }: FormFieldProps) {
  return (
    <div className={className}>
      <label className="mb-2 block text-sm font-medium text-foreground">
        {label}
        {required && <span className="ml-1 text-destructive">*</span>}
      </label>
      {children}
      {error && <p className="mt-1 text-sm text-destructive">{error}</p>}
    </div>
  );
}

// Grid helper for form layouts
interface FormGridProps {
  children: ReactNode;
  columns?: 1 | 2 | 3;
}

export function FormGrid({ children, columns = 2 }: FormGridProps) {
  const gridCols = {
    1: 'grid-cols-1',
    2: 'grid-cols-1 sm:grid-cols-2',
    3: 'grid-cols-1 sm:grid-cols-2 lg:grid-cols-3',
  };

  return <div className={`grid gap-4 ${gridCols[columns]}`}>{children}</div>;
}
