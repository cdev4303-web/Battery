import React, { Component, ErrorInfo, ReactNode } from 'react';
import { AlertTriangle, RefreshCw } from 'lucide-react';

interface Props {
  children: ReactNode;
}

interface State {
  hasError: boolean;
  error: Error | null;
}

export class ErrorBoundary extends Component<Props, State> {
  public state: State = {
    hasError: false,
    error: null,
  };

  public static getDerivedStateFromError(error: Error): State {
    return { hasError: true, error };
  }

  public componentDidCatch(error: Error, errorInfo: ErrorInfo) {
    console.error('Uncaught error in Charging Assistant:', error, errorInfo);
  }

  public handleReset = () => {
    this.setState({ hasError: false, error: null });
    window.location.reload();
  };

  public render() {
    if (this.state.hasError) {
      return (
        <div className="min-h-screen bg-slate-950 text-slate-100 flex flex-col items-center justify-center p-6 text-center">
          <div className="w-16 h-16 rounded-2xl bg-rose-500/20 text-rose-400 border border-rose-500/30 flex items-center justify-center mb-4 shadow-lg shadow-rose-500/10">
            <AlertTriangle className="w-8 h-8" />
          </div>
          <h1 className="text-xl font-bold text-white mb-2">চার্জিং সহকারী লোড করতে সমস্যা হয়েছে</h1>
          <p className="text-sm text-slate-400 max-w-sm mb-6 leading-relaxed">
            একটি অপ্রত্যাশিত ত্রুটি ঘটেছে। অনুগ্রহ করে পেজটি রিফ্রেশ করুন অথবা নিচের বোতামে চাপ দিন।
          </p>
          <button
            onClick={this.handleReset}
            className="flex items-center gap-2 px-5 py-2.5 rounded-xl bg-emerald-600 hover:bg-emerald-500 text-white font-semibold text-sm transition shadow-lg shadow-emerald-600/20"
          >
            <RefreshCw className="w-4 h-4" />
            <span>পুনরায় চেষ্টা করুন (Reload)</span>
          </button>
          {this.state.error && (
            <div className="mt-6 p-3 rounded-lg bg-slate-900 border border-slate-800 text-left text-xs text-rose-300 font-mono max-w-sm overflow-x-auto">
              {this.state.error.message}
            </div>
          )}
        </div>
      );
    }

    return this.props.children;
  }
}
