#include <sys/types.h>
#include <sys/ipc.h>
#include <sys/shm.h>
#include <errno.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include "extcalls.h"

static extblock *inblk = NULL;
static extblock *outblk = NULL;
static int outid;
static int maxparams;


void extterminate (char *msg)
{
  if (msg != NULL) fprintf (stderr, msg);
  perror ("extcalls");
  exit (1);
}

static void addaddr (extref **p)
{
  extref *q;
  int i;

  q = *p = (extref*) (((char *) inblk) + ((int) *p));
  if (q->type == EXTPARAM_LIST)
    for (i = 0; i < q->len; i++) addaddr (&(q->data.p[i]));
}

int extenter (char *arg1, int maxoutsize, int maxoutparams)
{
  int i;

  if (maxoutsize < 1024) maxoutsize = 1024;
  if (arg1 == NULL) { errno = EINVAL; extterminate (NULL); }
  inblk = (extblock*) shmat (atoi (arg1), NULL, 0);
  if ((int) inblk == -1) extterminate (NULL);
  for (i = 0; i < inblk->nrparam; i++) addaddr (&(inblk->p[i]));
  outid = inblk->inid;  inblk->inid = EXTID_INVAL;
  if (outid < 0) {
    outid = shmget (IPC_PRIVATE, maxoutsize, IPC_CREAT | 00600);
    if (outid < 0) extterminate (NULL);
    outblk = (extblock*) shmat (outid, NULL, 0);
    if ((int) outblk == -1) extterminate (NULL);
    outblk->size = maxoutsize;
  }
  else {
    outblk = (extblock*) shmat (outid, NULL, 0);
    if ((int) outblk == -1 || maxoutsize > outblk->size) {
      if ((int) outblk != -1)
	if (shmdt ((caddr_t) outblk) < 0 || shmctl (outid, IPC_RMID, 0) < 0)
	  extterminate (NULL);
      outid = shmget (IPC_PRIVATE, maxoutsize, IPC_CREAT | 00600);
      if (outid < 0) extterminate (NULL);
      outblk = (extblock*) shmat (outid, NULL, 0);
      if ((int) outblk == -1) extterminate (NULL);
      outblk->size = maxoutsize;
    }
  }
  outblk->nrparam = 0;
  outblk->end = sizeof (extblock) + (maxoutparams - 1) * sizeof (extref*);
  maxparams = maxoutparams;
  return (inblk->nrparam);
}

static void subaddr (extref **p)
{
  extref *q;
  int i;

  q = *p;
  *p = (extref*) (((char *) q) - ((char *) outblk));
  if (q->type == EXTPARAM_LIST)
    for (i = 0; i < q->len; i++) subaddr (&(q->data.p[i]));
}

void extexit ()
{
  int i;

  /* Change address to base 0 */
  for (i = 0; i < abs (outblk->nrparam); i++) subaddr (&(outblk->p[i]));
  inblk->inid = outid;
  if (shmdt ((caddr_t) inblk) < 0 || shmdt ((caddr_t) outblk) < 0)
    extterminate (NULL);
  exit (0);
}

static void extuserror2 (char *msg, char *msg2)
{
  extref *e;

  maxparams = 2;
  outblk->nrparam = 0;
  outblk->end = sizeof (extblock);
  if (msg2 != NULL) outblk->end += sizeof (extref*);
  extaddretval (extputtext (strlen (msg), msg));
  if (msg2 != NULL) extaddretval (extputtext (strlen (msg2), msg2));
  outblk->nrparam = -outblk->nrparam;
  extexit ();
}

void extuserror (char *msg)
{
  extuserror2 (msg, NULL);
}

extref *extget (int nr)
{
  char msg2[80];

  if (nr < 1 || nr > inblk->nrparam) {
    sprintf (msg2, "External function uses %d%s argument", nr,
	     nr <= 2 ? (nr == 1 ? "st" : "nd") : (nr == 3 ? "rd" : "th"));
    extuserror2 ("missing arguments", msg2);
  }
  return (inblk->p[nr-1]);
}

static void invalidarg (int nr, int type, int dim)
{
  char msg2[128];
  int i;

  sprintf (msg2, "External function expects %d%s argument: ", nr,
	   nr <= 2 ? (nr == 1 ? "st" : "nd") : (nr == 3 ? "rd" : "th"));
  for (i = 1; i < dim; i++) strcat (msg2, "array(");
  if (type == EXTPARAM_LIST) strcat (msg2, "list");
  else if (type == EXTPARAM_CHAR) strcat (msg2, "string");
  else if (type == EXTPARAM_INT) strcat (msg2, "{integer,array(integer)}");
  else if (type == EXTPARAM_DBL) strcat (msg2, "{numeric,array(numeric)}");
  for (i = 1; i < dim; i++) strcat (msg2, ")");
  extuserror2 ("invalid arguments", msg2);
}

extern int extindexerr (char *file, int line, char *name, int i, int len)
{
  char msg2[128];

  sprintf (msg2, "%s, line %d: var=%s, index=%d, len=%d",
	   file, line, name, i, len);
  extuserror2 ("array selector out of bounds", msg2);
  return (i);
}

void extmakedbl (extref **p)
{
  extref *e, *newe;
  int i, len;

  e = *p;
  if (e->type == EXTPARAM_INT) {
    len = e->len;
    newe = (extref *) malloc (sizeof (extref) + (len - 1) * sizeof (double));
    newe->type = EXTPARAM_DBL;
    newe->len = len;
    for (i = 0; i < len; i++) newe->data.x[i] = (double) e->data.n[i];
    *p = newe;
  }
}

static extref *extgetmdim (int nr, extref *e, int level, int dim, int type)
{
  int i;

  if (level < dim) {
    if (e->type != EXTPARAM_LIST) invalidarg (nr, type, dim);
    for (i = 0; i < e->len; i++)
      e->data.p[i] = extgetmdim (nr, e->data.p[i], level + 1, dim, type);
  }
  else {
    if (type == EXTPARAM_DBL) extmakedbl (&e);
    if (e->type != type) invalidarg (nr, type, dim);
  }
  return (e);
}

extref *extgetlist (int nr, int dim)
{
  return (extgetmdim (nr, extget (nr), 1, dim, EXTPARAM_LIST));
}

extref *extgettext (int nr, int dim)
{
  return (extgetmdim (nr, extget (nr), 1, dim, EXTPARAM_CHAR));
}

extref *extgetint (int nr, int dim)
{
  return (extgetmdim (nr, extget (nr), 1, dim, EXTPARAM_INT));
}

extref *extgetdbl (int nr, int dim)
{
  return (inblk->p[nr-1] = extgetmdim (nr, extget (nr), 1, dim, EXTPARAM_DBL));
}

void extaddretval (extref *p)
{
  if (outblk->nrparam >= maxparams) extuserror ("Too many returns values");
  outblk->p[outblk->nrparam++] = p;
}

extref *extalloclist (int len, extref ***dest)
{
  extref *e, **p;
  int i;

  outblk->end = ((outblk->end + sizeof (extref*) - 1) / sizeof (extref*))
    * sizeof (extref*);
  e = (extref *) (((char *) outblk) + outblk->end);
  outblk->end += sizeof (extref) + (len - 1) * sizeof (extref*);
  if (outblk->end > outblk->size) extuserror ("Return values exceed size");
  e->type = EXTPARAM_LIST;
  e->len = len;
  p = *dest = e->data.p;
  for (i = 0; i < len; i++) p[i] = NULL;
  return (e);
}

extref *extalloctext (int len, char **dest)
{
  extref *e;

  outblk->end = ((outblk->end + sizeof (extref*) - 1) / sizeof (extref*))
    * sizeof (extref*);
  e = (extref *) (((char *) outblk) + outblk->end);
  outblk->end += sizeof (extref) + (len - 1) * sizeof (char);
  if (outblk->end > outblk->size) extterminate ("Return values exceed size");
  e->type = EXTPARAM_CHAR;
  e->len = len;
  *dest = e->data.c;
  return (e);
}

extref *extallocint (int len, int **dest)
{
  extref *e;

  outblk->end = ((outblk->end + sizeof (extref*) - 1) / sizeof (extref*))
    * sizeof (extref*);
  e = (extref *) (((char *) outblk) + outblk->end);
  outblk->end += sizeof (extref) + (len - 1) * sizeof (int);
  if (outblk->end > outblk->size) extterminate ("Return values exceed size");
  e->type = EXTPARAM_INT;
  e->len = len;
  *dest = e->data.n;
  return (e);
}

extref *extallocdbl (int len, double **dest)
{
  extref *e;

  outblk->end = ((outblk->end + sizeof (double) - 1) / sizeof (double))
    * sizeof (double);
  e = (extref *) (((char *) outblk) + outblk->end);
  outblk->end += sizeof (extref) + (len - 1) * sizeof (double);
  if (outblk->end > outblk->size) extterminate ("Return values exceed size");
  e->type = EXTPARAM_DBL;
  e->len = len;
  *dest = e->data.x;
  return (e);
}

extref *extputtext (int len, char *c)
{
  extref *e;
  char *dest;

  e = extalloctext (len, &dest);
  strncpy (dest, c, len);
  dest[len] = 0;
  return (e);
}

extref *extputint (int len, int *n)
{
  extref *e;
  int i, *dest;

  e = extallocint (len, &dest);
  for (i = 0; i < len; i++) dest[i] = n[i];
  return (e);
}

extref *extputdbl (int len, double *x)
{
  extref *e;
  int i;
  double *dest;

  e = extallocdbl (len, &dest);
  for (i = 0; i < len; i++) dest[i] = x[i];
  return (e);
}
