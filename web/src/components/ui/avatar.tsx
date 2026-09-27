'use client';

import * as React from 'react';
import { cn } from '@/lib/utils';

interface AvatarProps extends React.HTMLDivElement {
  delay?: number;
}

const Avatar = React.forwardRef<HTMLDivElement, AvatarProps>(
  ({ className, ...props }, ref) => (
    <div
      ref={ref}
      className={cn('relative flex h-10 w-10 shrink-0 overflow-hidden rounded-full', className)}
      {...props}
    />
  )
);
Avatar.displayName = 'Avatar';

interface AvatarImageProps extends React.HTMLImageElement {
  delay?: number;
}

const AvatarImage = React.forwardRef<HTMLImageElement, AvatarImageProps>(
  ({ className, delay = 0, ...props }, ref) => (
    <img
      ref={ref}
      className={cn('aspect-square h-full w-full object-cover', className)}
      loading={delay > 0 ? 'lazy' : 'eager'}
      {...props}
    />
  )
);
AvatarImage.displayName = 'AvatarImage';

interface AvatarFallbackProps extends React.HTMLSpanElement {
  delay?: number;
}

const AvatarFallback = React.forwardRef<HTMLSpanElement, AvatarFallbackProps>(
  ({ className, delay = 0, ...props }, ref) => (
    <span
      ref={ref}
      className={cn(
        'flex h-full w-full items-center justify-center rounded-full bg-muted',
        'text-sm font-medium text-muted-foreground',
        className
      )}
      {...props}
    />
  )
);
AvatarFallback.displayName = 'AvatarFallback';

export { Avatar, AvatarImage, AvatarFallback };