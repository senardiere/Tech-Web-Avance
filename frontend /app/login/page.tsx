'use client';

import { useState } from 'react';
import { useRouter } from 'next/navigation';
import { useAuth } from '@/lib/auth-context';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/card';
import { Stethoscope, Eye, EyeOff, AlertCircle, Loader2 } from 'lucide-react';
import { FieldGroup, Field, FieldLabel } from '@/components/ui/field';

export default function LoginPage() {
  const [login, setLogin] = useState('');
  const [password, setPassword] = useState('');
  const [showPassword, setShowPassword] = useState(false);
  const [error, setError] = useState('');
  const [isLoading, setIsLoading] = useState(false);
  const router = useRouter();
  const { login: authLogin } = useAuth();

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError('');
    setIsLoading(true);

    try {
      await authLogin(login, password);
      router.push('/dashboard');
    } catch (err) {
      setError('Identifiants invalides. Veuillez reessayer.');
      console.error(err);
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <div className="min-h-screen flex">
      {/* Left Panel - Branding */}
      <div className="hidden lg:flex lg:flex-1 flex-col justify-between bg-sidebar p-12">
        <div>
          <div className="flex items-center gap-3">
            <div className="flex h-10 w-10 items-center justify-center rounded-xl bg-sidebar-primary">
              <Stethoscope className="h-5 w-5 text-sidebar-primary-foreground" />
            </div>
            <span className="text-xl font-semibold text-sidebar-foreground">
              MediCare
            </span>
          </div>
        </div>

        <div className="space-y-6">
          <h1 className="text-4xl font-bold leading-tight text-sidebar-foreground">
            Gerez votre clinique
            <br />
            <span className="text-sidebar-primary">en toute simplicite</span>
          </h1>
          <p className="text-lg text-sidebar-foreground/70 max-w-md">
            Une solution complete pour la gestion de vos patients, rendez-vous et consultations medicales.
          </p>
          <div className="space-y-4 pt-4">
            <FeatureItem text="Gestion complete des dossiers patients" />
            <FeatureItem text="Planification intelligente des rendez-vous" />
            <FeatureItem text="Suivi des consultations en temps reel" />
            <FeatureItem text="Interface intuitive et securisee" />
          </div>
        </div>

        <p className="text-sm text-sidebar-foreground/50">
          2024 MediCare. Tous droits reserves.
        </p>
      </div>

      {/* Right Panel - Login Form */}
      <div className="flex flex-1 items-center justify-center bg-background p-6">
        <div className="w-full max-w-[400px]">
          {/* Mobile Logo */}
          <div className="mb-8 flex items-center gap-3 lg:hidden">
            <div className="flex h-10 w-10 items-center justify-center rounded-xl bg-primary">
              <Stethoscope className="h-5 w-5 text-primary-foreground" />
            </div>
            <span className="text-xl font-semibold text-foreground">MediCare</span>
          </div>

          <Card className="border-0 shadow-none lg:border lg:shadow-sm">
            <CardHeader className="space-y-1 px-0 lg:px-6">
              <CardTitle className="text-2xl font-bold">Connexion</CardTitle>
              <CardDescription>
                Entrez vos identifiants pour acceder a votre espace
              </CardDescription>
            </CardHeader>
            <CardContent className="px-0 lg:px-6">
              {error && (
                <div className="mb-4 flex items-center gap-2 rounded-lg border border-destructive/20 bg-destructive/10 p-3 text-sm text-destructive">
                  <AlertCircle className="h-4 w-4 shrink-0" />
                  {error}
                </div>
              )}

              <form onSubmit={handleSubmit}>
                <FieldGroup>
                  <Field>
                    <FieldLabel htmlFor="login">Identifiant</FieldLabel>
                    <Input
                      id="login"
                      type="text"
                      value={login}
                      onChange={(e) => setLogin(e.target.value)}
                      required
                      autoFocus
                      placeholder="Votre identifiant"
                      className="h-11"
                    />
                  </Field>

                  <Field>
                    <FieldLabel htmlFor="password">Mot de passe</FieldLabel>
                    <div className="relative">
                      <Input
                        id="password"
                        type={showPassword ? 'text' : 'password'}
                        value={password}
                        onChange={(e) => setPassword(e.target.value)}
                        required
                        placeholder="Votre mot de passe"
                        className="h-11 pr-10"
                      />
                      <button
                        type="button"
                        onClick={() => setShowPassword(!showPassword)}
                        className="absolute right-3 top-1/2 -translate-y-1/2 text-muted-foreground hover:text-foreground transition-colors"
                      >
                        {showPassword ? (
                          <EyeOff className="h-4 w-4" />
                        ) : (
                          <Eye className="h-4 w-4" />
                        )}
                      </button>
                    </div>
                  </Field>
                </FieldGroup>

                <Button
                  type="submit"
                  disabled={isLoading}
                  className="mt-6 w-full h-11"
                >
                  {isLoading ? (
                    <>
                      <Loader2 className="mr-2 h-4 w-4 animate-spin" />
                      Connexion en cours...
                    </>
                  ) : (
                    'Se connecter'
                  )}
                </Button>
              </form>

              {/* <div className="mt-6 rounded-lg border border-border bg-muted/50 p-4">
                <p className="text-xs font-medium text-muted-foreground mb-2">
                  Identifiants de demonstration
                </p>
                <div className="space-y-1 text-sm">
                  <p className="flex items-center justify-between">
                    <span className="text-muted-foreground">Identifiant:</span>
                    <code className="rounded bg-background px-2 py-0.5 text-foreground">admin</code>
                  </p>
                  <p className="flex items-center justify-between">
                    <span className="text-muted-foreground">Mot de passe:</span>
                    <code className="rounded bg-background px-2 py-0.5 text-foreground">admin123</code>
                  </p>
                </div>
              </div> */}
            </CardContent>
          </Card>
        </div>
      </div>
    </div>
  );
}

function FeatureItem({ text }: { text: string }) {
  return (
    <div className="flex items-center gap-3">
      <div className="flex h-6 w-6 items-center justify-center rounded-full bg-sidebar-primary/20">
        <div className="h-2 w-2 rounded-full bg-sidebar-primary" />
      </div>
      <span className="text-sidebar-foreground/80">{text}</span>
    </div>
  );
}
